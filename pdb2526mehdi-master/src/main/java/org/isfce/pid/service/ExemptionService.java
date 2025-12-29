package org.isfce.pid.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.isfce.pid.dao.IExemptionItemDao;
import org.isfce.pid.dao.IExemptionRequestDao;
import org.isfce.pid.dao.IExternalCourseDao;
import org.isfce.pid.dao.ISectionDao;
import org.isfce.pid.dao.ISupportingDocumentDao;
import org.isfce.pid.dao.IUeDao;
import org.isfce.pid.dto.AddCourseDocumentDto;
import org.isfce.pid.dto.AddExternalCourseDto;
import org.isfce.pid.dto.AddManualExemptionItemDto;
import org.isfce.pid.dto.AddSupportingDocumentDto;
import org.isfce.pid.dto.CreateExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestFullDto;
import org.isfce.pid.dto.ExternalCourseDto;
import org.isfce.pid.dto.SupportingDocumentDto;
import org.isfce.pid.mapper.ExemptionMapper;
import org.isfce.pid.model.DecisionItem;
import org.isfce.pid.model.ExemptionItem;
import org.isfce.pid.model.ExemptionRequest;
import org.isfce.pid.model.ExternalCourse;
import org.isfce.pid.model.KbCorrespondenceRuleTarget;
import org.isfce.pid.model.Section;
import org.isfce.pid.model.StatutDemande;
import org.isfce.pid.model.Student;
import org.isfce.pid.model.SupportingDocument;
import org.isfce.pid.model.TypeDocument;
import org.isfce.pid.model.UE;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service métier gérant le cycle de vie complet des demandes de dispenses.
 * Responsabilités :
 * 	Création et gestion des brouillons.
 * 	Gestion des preuves (cours externes et documents).
 * 	Orchestration du moteur de règles (KnowledgeBase) pour l'analyse automatique.
 * 	Validation et soumission finale des dossiers.
 * Les messages d'erreurs sont internationalisés via {@link MessageSource}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ExemptionService {

    private final IExemptionRequestDao reqDao;
    private final IExternalCourseDao extCourseDao;
    private final ISupportingDocumentDao docDao;
    private final IExemptionItemDao itemDao;
    private final ISectionDao sectionDao;
    private final IUeDao ueDao;

    private final StudentService studentService;
    private final KnowledgeBaseService kbService;
    private final ExemptionMapper mapper;
    private final MessageSource messageSource;

    /**
     * Crée une nouvelle demande de dispense à l'état de brouillon.
     *
     * @param dto Contient l'email de l'étudiant et le code de la section visée.
     * @return Le DTO de la demande créée.
     * @throws IllegalArgumentException Si la section indiquée n'existe pas.
     */
    public ExemptionRequestDto createDraft(CreateExemptionRequestDto dto) {
        Section section = sectionDao.findById(dto.getSectionCode())
                .orElseThrow(() -> new IllegalArgumentException(msg("err.section.notFound", dto.getSectionCode())));

        Student student = studentService.getOrCreateByEmail(dto.getEmail());

        ExemptionRequest req = ExemptionRequest.builder()
                .etudiant(student)
                .section(section)
                .statut(StatutDemande.DRAFT)
                .build();

        return mapper.toExemptionRequestDto(reqDao.save(req));
    }

    /**
     * Ajoute un cours externe (réussi dans un autre établissement) au dossier en cours.
     *
     * @param requestId Identifiant de la demande.
     * @param dto       Détails du cours externe (Code, ECTS, Libellé...).
     * @return Le DTO du cours externe ajouté.
     */
    public ExternalCourseDto addExternalCourse(UUID requestId, AddExternalCourseDto dto) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        ExternalCourse course = ExternalCourse.builder()
                .request(req)
                .etablissement(dto.getEtablissement())
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .ects(dto.getEcts())
                .urlProgramme(dto.getUrlProgramme())
                .build();

        return mapper.toExternalCourseDto(extCourseDao.save(course));
    }

    /**
     * Ajoute un document justificatif global à la demande (ex: Bulletin, Diplôme).
     *
     * @param requestId Identifiant de la demande.
     * @param dto       Détails du document (URL, Type).
     * @return Le DTO du document sauvegardé.
     */
    public SupportingDocumentDto addGlobalDocument(UUID requestId, AddSupportingDocumentDto dto) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        SupportingDocument d = SupportingDocument.builder()
                .request(req)
                .type(dto.getType())
                .urlStockage(dto.getUrlStockage())
                .originalFileName(dto.getOriginalFileName())
                .build();

        return mapper.toSupportingDocumentDto(docDao.save(d));
    }

    /**
     * Ajoute un document spécifique lié à un cours externe (ex: Programme de cours).
     *
     * @param externalCourseId Identifiant du cours externe concerné.
     * @param dto              Détails du document.
     * @return Le DTO du document sauvegardé.
     * @throws IllegalStateException Si la demande parente n'est plus en brouillon.
     */
    public SupportingDocumentDto addCourseDocument(UUID externalCourseId, AddCourseDocumentDto dto) {
        ExternalCourse course = extCourseDao.findById(externalCourseId)
                .orElseThrow(() -> new IllegalArgumentException(msg("err.externalCourse.notFound")));

        if (course.getRequest().getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException(msg("err.request.locked", course.getRequest().getStatut()));
        }

        SupportingDocument d = SupportingDocument.builder()
                .externalCourse(course)
                .type(TypeDocument.PROGRAMME)
                .urlStockage(dto.getUrlStockage())
                .originalFileName(dto.getOriginalFileName())
                .build();

        return mapper.toSupportingDocumentDto(docDao.save(d));
    }

    /**
     * Permet à l'étudiant de solliciter manuellement une dispense pour une UE spécifique.
     *
     * @param requestId Identifiant de la demande.
     * @param dto       Contient l'UE visée et les IDs des cours externes justificatifs.
     * @return Le dossier complet mis à jour.
     * @throws SecurityException Si l'étudiant tente d'utiliser des cours n'appartenant pas à sa demande.
     */
    public ExemptionRequestFullDto addManualItem(UUID requestId, AddManualExemptionItemDto dto) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        UE ue = ueDao.findById(dto.getUeCode())
                .orElseThrow(() -> new IllegalArgumentException(msg("err.ue.notFound", dto.getUeCode())));

        boolean exists = req.getItems().stream()
                .anyMatch(i -> i.getUe().getCode().equals(ue.getCode()));
        if (exists) {
            throw new IllegalArgumentException(msg("err.item.duplicate", ue.getCode()));
        }

        List<ExternalCourse> justifCourses = extCourseDao.findAllById(dto.getExternalCourseIds());
        if (justifCourses.isEmpty()) {
            throw new IllegalArgumentException(msg("err.externalCourse.noneValid"));
        }

        boolean allBelongToRequest = justifCourses.stream()
                .allMatch(c -> c.getRequest().getId().equals(requestId));
        if (!allBelongToRequest) {
            throw new SecurityException(msg("err.security.courseOwnership"));
        }

        ExemptionItem item = ExemptionItem.builder()
                .request(req)
                .ue(ue)
                .decision(DecisionItem.PENDING)
                .justifyingCourses(new HashSet<>(justifCourses))
                .build();

        int sumEcts = justifCourses.stream().mapToInt(ExternalCourse::getEcts).sum();
        item.setTotalEctsMatches(sumEcts >= ue.getEcts());

        itemDao.save(item);

        if (req.getItems() == null) {
            req.setItems(new HashSet<>());
        }
        req.getItems().add(item);

        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    /**
     * Lance le moteur de règles (KnowledgeBase) pour détecter automatiquement les dispenses possibles.
     *
     * @param requestId Identifiant de la demande.
     * @return Le dossier mis à jour avec les propositions acceptées automatiquement.
     */
    public ExemptionRequestFullDto analyzeRequest(UUID requestId) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        List<KnowledgeBaseService.RuleMatch> matches = kbService.findMatchingRules(req.getExternalCourses());

        for (KnowledgeBaseService.RuleMatch match : matches) {
            for (KbCorrespondenceRuleTarget target : match.rule().getTargets()) {
                UE ueCible = target.getUe();

                boolean exists = req.getItems().stream()
                        .anyMatch(i -> i.getUe().getCode().equals(ueCible.getCode()));

                if (!exists) {
                    ExemptionItem newItem = ExemptionItem.builder()
                            .request(req)
                            .ue(ueCible)
                            .decision(DecisionItem.AUTO_ACCEPTED)
                            .totalEctsMatches(true)
                            .justifyingCourses(match.studentCourses())
                            .build();

                    itemDao.save(newItem);
                    req.addItem(newItem);
                }
            }
        }

        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    /**
     * Valide et soumet la demande. Le statut passe de DRAFT à SUBMITTED.
     *
     * @param requestId Identifiant de la demande.
     * @return La demande soumise.
     * @throws IllegalStateException Si la demande est invalide (vide, cours orphelins, preuves manquantes).
     */
    public ExemptionRequestFullDto submitRequest(UUID requestId) {
        ExemptionRequest req = reqDao.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(msg("err.request.notFound")));

        if (req.getStatut() == StatutDemande.SUBMITTED) {
            throw new IllegalStateException(msg("err.submission.alreadySubmitted"));
        }

        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException(msg("err.request.locked", req.getStatut()));
        }

        validateSubmission(req);

        req.setStatut(StatutDemande.SUBMITTED);
        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    /**
     * Récupère toutes les demandes associées à un étudiant.
     *
     * @param email Email de l'étudiant.
     * @return Liste des demandes (format léger).
     */
    @Transactional(readOnly = true)
    public List<ExemptionRequestDto> myRequests(String email) {
        return reqDao.findAllByEtudiantEmail(email).stream()
                .map(mapper::toExemptionRequestDto)
                .toList();
    }

    /**
     * Récupère le détail complet d'une demande spécifique.
     *
     * @param requestId Identifiant de la demande.
     * @return Le DTO complet incluant cours, documents et items.
     */
    public ExemptionRequestFullDto getRequestDetail(UUID requestId) {
        ExemptionRequest req = reqDao.findWithAllById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(msg("err.request.notFound")));
        return mapper.toExemptionRequestFullDto(req);
    }

    /**
     * Supprime définitivement un brouillon et toutes ses dépendances.
     *
     * @param requestId Identifiant de la demande.
     */
    public void deleteDraft(UUID requestId) {
        ExemptionRequest req = getDraftOrThrow(requestId);
        reqDao.delete(req);
    }

    /**
     * Supprime un document justificatif.
     *
     * @param docId Identifiant du document.
     */
    public void deleteDocument(UUID docId) {
        SupportingDocument doc = docDao.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException(msg("err.document.notFound")));

        ExemptionRequest req = doc.getRequest() != null ? doc.getRequest() : doc.getExternalCourse().getRequest();
        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException(msg("err.request.locked", req.getStatut()));
        }

        docDao.delete(doc);
    }

    /**
     * Supprime une ligne de dispense spécifique.
     *
     * @param itemId Identifiant de l'item de dispense.
     * @return La demande mise à jour.
     */
    public ExemptionRequestFullDto deleteItem(UUID itemId) {
        ExemptionItem item = itemDao.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(msg("err.item.notFound")));

        ExemptionRequest req = item.getRequest();

        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException(msg("err.request.locked", req.getStatut()));
        }

        req.getItems().remove(item);
        itemDao.delete(item);

        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    // ————— MÉTHODES PRIVÉES (Helpers) —————

    /**
     * Récupère une demande si elle existe et est en statut DRAFT.
     *
     * @throws IllegalStateException Si la demande n'est pas en brouillon.
     */
    private ExemptionRequest getDraftOrThrow(UUID id) {
        ExemptionRequest req = reqDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(msg("err.request.notFound")));

        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException(msg("err.request.locked", req.getStatut()));
        }
        return req;
    }

    /**
     * Vérifie l'intégrité métier du dossier avant soumission.
     * Contrôle : dossier vide, orphelins (cours non utilisés), documents manquants.
     */
    private void validateSubmission(ExemptionRequest req) {
        if (req.getExternalCourses().isEmpty()) {
            throw new IllegalStateException(msg("err.submission.empty"));
        }

        if (req.getItems().isEmpty()) {
            throw new IllegalStateException(msg("err.submission.noItems"));
        }

        Set<UUID> allExternalIds = req.getExternalCourses().stream()
                .map(ExternalCourse::getId)
                .collect(Collectors.toSet());

        Set<UUID> usedExternalIds = req.getItems().stream()
                .flatMap(item -> item.getJustifyingCourses().stream())
                .map(ExternalCourse::getId)
                .collect(Collectors.toSet());

        if (!usedExternalIds.containsAll(allExternalIds)) {
            allExternalIds.removeAll(usedExternalIds);
            throw new IllegalStateException(msg("err.submission.orphans", allExternalIds.size()));
        }

        boolean hasGlobalDoc = req.getGlobalDocuments().stream()
                .anyMatch(d -> d.getExternalCourse() == null);

        boolean allCoursesHaveProof = req.getExternalCourses().stream()
                .allMatch(c -> !c.getDocuments().isEmpty());

        if (!hasGlobalDoc && !allCoursesHaveProof) {
            throw new IllegalStateException(msg("err.submission.missingDocs"));
        }
    }

    /**
     * Utilitaire interne pour récupérer un message internationalisé.
     *
     * @param code La clé du message dans le fichier properties.
     * @param args Les arguments optionnels pour formater le message.
     * @return La chaîne traduite.
     */
    private String msg(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}