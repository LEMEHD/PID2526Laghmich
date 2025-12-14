package org.isfce.pid.service;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dao.*;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.mapper.ExemptionMapper;
import org.isfce.pid.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ExemptionService {

    // DAOs liés au processus de demande
    private final IExemptionRequestDao reqDao;
    private final IExternalCourseDao extCourseDao;
    private final ISupportingDocumentDao docDao;
    private final IExemptionItemDao itemDao;

    // Services métiers délégués
    private final StudentService studentService;
    private final KnowledgeBaseService kbService;
    
    // Le Mapper
    private final ExemptionMapper mapper;

    // ————— GESTION DU BROUILLON (DRAFT) —————

    public ExemptionRequest createDraft(String email, Section section) {
        Student student = studentService.getOrCreateByEmail(email);
        
        ExemptionRequest req = ExemptionRequest.builder()
                .etudiant(student)
                .section(section)
                .statut(StatutDemande.DRAFT)
                .build();
                
        return reqDao.save(req);
    }

    public ExternalCourse addExternalCourse(UUID requestId, String etab, String code, String libelle, int ects, String url) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        ExternalCourse c = ExternalCourse.builder()
                .request(req)
                .etablissement(etab)
                .code(code)
                .libelle(libelle)
                .ects(ects)
                .urlProgramme(url)
                .build();

        return extCourseDao.save(c);
    }

    /**
     * Ajout d'un document GLOBAL (ex: Bulletin, Diplôme).
     * Ces documents sont liés à la demande elle-même.
     */
    public SupportingDocument addGlobalDocument(UUID requestId, TypeDocument type, String url) {
        ExemptionRequest req = getDraftOrThrow(requestId);
        
        // Attention : Assure-toi que ton entité SupportingDocument a bien le champ 'request'
        SupportingDocument d = SupportingDocument.builder()
                .request(req) 
                .type(type)
                .urlStockage(url)
                .build();
        return docDao.save(d);
    }

    /**
     * Ajout d'un document SPÉCIFIQUE (ex: Programme de cours, Table des matières).
     * Ces documents sont liés à un cours externe précis.
     */
    public SupportingDocument addCourseDocument(UUID externalCourseId, String url) {
        ExternalCourse course = extCourseDao.findById(externalCourseId)
                .orElseThrow(() -> new IllegalArgumentException("Cours externe introuvable"));

        // Vérifier que la demande associée est bien en DRAFT
        if (course.getRequest().getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Impossible d'ajouter un document : demande verrouillée");
        }

        // Attention : Assure-toi que ton entité SupportingDocument a bien le champ 'externalCourse'
        SupportingDocument d = SupportingDocument.builder()
                .externalCourse(course) 
                .type(TypeDocument.PROGRAMME) 
                .urlStockage(url)
                .build();

        return docDao.save(d);
    }

    /**
     * Permet à l'étudiant de déclarer MANUELLEMENT une demande de dispense pour une UE spécifique.
     * (Étape 3 du flux : "Le système n'a pas trouvé, mais je veux quand même demander une dispense pour IPAP")
     */
    public ExemptionItem addManualItem(UUID requestId, UE ueCible, Set<UUID> externalCourseIds) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        // On récupère les cours externes sélectionnés
        Set<ExternalCourse> selectedCourses = new HashSet<>(extCourseDao.findAllById(externalCourseIds));

        // Validation : les cours doivent appartenir à la demande
        if (!selectedCourses.stream().allMatch(c -> c.getRequest().getId().equals(requestId))) {
            throw new IllegalArgumentException("Certains cours sélectionnés n'appartiennent pas à cette demande");
        }

        ExemptionItem item = ExemptionItem.builder()
                .request(req)
                .ue(ueCible)
                .justifyingCourses(selectedCourses)
                .decision(DecisionItem.PENDING) // En attente de soumission/validation
                .build();

        return itemDao.save(item);
    }

    // ————— NOUVELLE ÉTAPE : ANALYSE (Bouton "Analyser mes cours") —————

    /**
     * Lance le moteur de règles sur les cours encodés et génère automatiquement
     * les items de dispense acceptés (AUTO_ACCEPTED).
     */
    public ExemptionRequestDto analyzeRequest(UUID requestId) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        // 1. Appel au moteur avec retour enrichi (Règle + Cours utilisés)
        List<KnowledgeBaseService.RuleMatch> matches = kbService.findMatchingRules(req.getExternalCourses());

        // 2. Création des items automatiques
        for (KnowledgeBaseService.RuleMatch match : matches) {
            for (KbCorrespondenceRuleTarget target : match.rule().getTargets()) {
                UE ueCible = target.getUe();

                // On vérifie si une ligne existe déjà pour cette UE (pour éviter les doublons)
                boolean exists = req.getItems().stream()
                        .anyMatch(i -> i.getUe().getCode().equals(ueCible.getCode()));

                if (!exists) {
                    ExemptionItem newItem = ExemptionItem.builder()
                            .request(req)
                            .ue(ueCible)
                            .decision(DecisionItem.AUTO_ACCEPTED) // C'est le système qui propose !
                            .totalEctsMatches(true) // Par définition de la règle KB
                            .justifyingCourses(match.studentCourses()) // On lie les cours précis trouvés par le moteur
                            .build();

                    itemDao.save(newItem);
                    req.addItem(newItem); // Mise à jour de l'objet en mémoire pour le retour DTO
                }
            }
        }
        
        // On renvoie l'état à jour
        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    // ————— ÉTAPE FINALE : SOUMISSION (Bouton "Envoyer") —————

    /**
     * Valide et verrouille la demande. 
     * Ne fait PLUS de calculs de correspondance (c'est le rôle de analyzeRequest).
     */
    public ExemptionRequestDto submit(UUID id) {
        ExemptionRequest req = reqDao.findWithAllById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Demande déjà soumise");
        }

        // 1. Validations métier
        validateDocuments(req);

        // 2. Gestion du statut des Items
        // Tous ceux qui n'ont pas été AUTO_ACCEPTED (donc ajoutés manuellement) passent en NEEDS_REVIEW
        for (ExemptionItem item : req.getItems()) {
            if (item.getDecision() == DecisionItem.PENDING) {
                item.setDecision(DecisionItem.NEEDS_REVIEW);
            }
            // Les AUTO_ACCEPTED restent tels quels
        }

        // 3. Changement de statut global
        req.setStatut(StatutDemande.SUBMITTED);
        req.setUpdatedAt(Instant.now());

        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    /**
     * Helper de validation des documents requis.
     * Règle : Soit un bulletin global, soit une preuve par cours.
     */
    private void validateDocuments(ExemptionRequest req) {
        if (req.getExternalCourses().isEmpty()) {
            throw new IllegalStateException("Veuillez ajouter au moins un cours externe.");
        }
        
        if (req.getItems().isEmpty()) {
             // Cas rare : l'élève a mis des cours mais n'a sélectionné aucune dispense
             throw new IllegalStateException("Veuillez sélectionner au moins une dispense (via l'analyse ou manuellement).");
        }

        // Note: Suppose que tu as renommé la liste dans l'entité ExemptionRequest en 'globalDocuments'
        boolean hasGlobalDoc = !req.getGlobalDocuments().isEmpty(); 
        
        // Suppose que ExternalCourse a une liste 'documents'
        boolean allCoursesHaveProof = req.getExternalCourses().stream()
                .allMatch(c -> c.getDocuments() != null && !c.getDocuments().isEmpty());

        if (!hasGlobalDoc && !allCoursesHaveProof) {
            throw new IllegalStateException(
                "Preuves manquantes : Fournissez un bulletin global OU une preuve pour CHAQUE cours encodé."
            );
        }
    }

    // ————— UTILITAIRES —————

    private ExemptionRequest getDraftOrThrow(UUID id) {
        ExemptionRequest req = reqDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));
        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Modification impossible : demande non brouillon");
        }
        return req;
    }

    @Transactional(readOnly = true)
    public List<ExemptionRequestDto> myRequests(String email) {
        return reqDao.findAllByEtudiantEmail(email).stream()
                .map(mapper::toExemptionRequestDto)
                .toList();
    }
    
    public void deleteRequest(UUID id) {
        ExemptionRequest req = getDraftOrThrow(id);
        reqDao.delete(req);
    }
}