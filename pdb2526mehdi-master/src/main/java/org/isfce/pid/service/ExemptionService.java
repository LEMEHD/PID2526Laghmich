package org.isfce.pid.service;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dao.*;
import org.isfce.pid.dto.*;
import org.isfce.pid.mapper.ExemptionMapper;
import org.isfce.pid.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    // ————— GESTION DU BROUILLON (DRAFT) —————

    public ExemptionRequestDto createDraft(CreateExemptionRequestDto dto) {
    	// Recherche de la section
        Section section = sectionDao.findById(dto.getSectionCode())
                .orElseThrow(() -> new IllegalArgumentException("Section introuvable: " + dto.getSectionCode()));
        // Gestion de l'étudiant
        Student student = studentService.getOrCreateByEmail(dto.getEmail());
        // Création de l'entité
        ExemptionRequest req = ExemptionRequest.builder()
                .etudiant(student)
                .section(section)
                .statut(StatutDemande.DRAFT)
                .build();
        // Sauvegarde et Mapping vers DTO
        return mapper.toExemptionRequestDto(reqDao.save(req));
    }

    public ExternalCourseDto addExternalCourse(UUID requestId, AddExternalCourseDto dto) {
    	// Vérif : La demande existe et est modifiable
    	ExemptionRequest req = getDraftOrThrow(requestId);
    	// Création de l'entité
        ExternalCourse course = ExternalCourse.builder()
                .request(req)
                .etablissement(dto.getEtablissement())
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .ects(dto.getEcts())
                .urlProgramme(dto.getUrlProgramme())
                .build();
        // Sauvegarde et Mapping vers DTO
        return mapper.toExternalCourseDto(extCourseDao.save(course));
    }

    // Ajout d'un document GLOBAL (ex: Bulletin, Diplôme,..).
    // Ces documents sont liés à la demande elle-même.
    public SupportingDocumentDto addGlobalDocument(UUID requestId, AddSupportingDocumentDto dto) {
    	ExemptionRequest req = getDraftOrThrow(requestId);
        SupportingDocument d = SupportingDocument.builder()
                .request(req)
                .type(dto.getType())
                .urlStockage(dto.getUrlStockage())
                .originalFileName(dto.getOriginalFileName()) // <--- ICI
                .build();
        return mapper.toSupportingDocumentDto(docDao.save(d));
    }

    // Ajout d'un document SPÉCIFIQUE (Programme de cours)
    // Ces documents sont liés à un cours externe précis.
    public SupportingDocumentDto addCourseDocument(UUID externalCourseId, AddCourseDocumentDto dto) {
    	ExternalCourse course = extCourseDao.findById(externalCourseId)
                .orElseThrow(() -> new IllegalArgumentException("Cours externe introuvable"));
        if (course.getRequest().getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Demande verrouillée");
        }
        SupportingDocument d = SupportingDocument.builder()
                .externalCourse(course) 
                // type forcé a PROGRAMME
                .type(TypeDocument.PROGRAMME)
                .urlStockage(dto.getUrlStockage())
                .originalFileName(dto.getOriginalFileName()) // <--- ICI
                .build();
        return mapper.toSupportingDocumentDto(docDao.save(d));
    }
    
    // Permet à l'étudiant de déclarer MANUELLEMENT une demande de dispense pour une UE spécifique.
    // (Le système n'a pas trouvé, mais je veux quand même demander une dispense pour IPAP)
    public ExemptionRequestFullDto addManualItem(UUID requestId, AddManualExemptionItemDto dto) {
        ExemptionRequest req = getDraftOrThrow(requestId); 
        // Récupérer l'UE cible
        UE ue = ueDao.findById(dto.getUeCode())
                .orElseThrow(() -> new IllegalArgumentException("UE introuvable : " + dto.getUeCode()));

        // Vérif doublon
        boolean exists = req.getItems().stream()
                .anyMatch(i -> i.getUe().getCode().equals(ue.getCode()));
        if (exists) {
            throw new IllegalArgumentException("Une demande pour cette UE existe déjà.");
        }
        
        // Récupérer les cours externes justificatifs
        List<ExternalCourse> justifCourses = extCourseDao.findAllById(dto.getExternalCourseIds());
        if (justifCourses.isEmpty()) {
            throw new IllegalArgumentException("Aucun cours justificatif valide trouvé.");
        }
        
        // Sécurité : Vérifier que ces cours appartiennent bien à CETTE demande
        // (Pour éviter qu'un étudiant utilise les cours d'un autre)
        boolean allBelongToRequest = justifCourses.stream()
                .allMatch(c -> c.getRequest().getId().equals(requestId));
        if (!allBelongToRequest) {
             throw new SecurityException("Tentative d'utilisation de cours n'appartenant pas au dossier.");
        }
        
        // Création de la ligne "En attente"
        ExemptionItem item = ExemptionItem.builder()
                .request(req)
                .ue(ue)
                .decision(DecisionItem.PENDING)
                .justifyingCourses(new HashSet<>(justifCourses))
                .build();
        
        // On pré-calcule si les crédits suffisent
        int sumEcts = justifCourses.stream().mapToInt(ExternalCourse::getEcts).sum();
        item.setTotalEctsMatches(sumEcts >= ue.getEcts());
        
        // Sauvegarde de la ligne item
        itemDao.save(item);
        
        // On renvoie le dossier complet mis à jour
        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    // ————— ANALYSE —————

    public ExemptionRequestFullDto analyzeRequest(UUID requestId) {
        ExemptionRequest req = getDraftOrThrow(requestId);
        
        // Appel au "Cerveau" : Le KnowledgeBaseService
        // Il nous rend une liste de "Match" (Règle validée + Les cours qui ont servi à la valider)
        List<KnowledgeBaseService.RuleMatch> matches = kbService.findMatchingRules(req.getExternalCourses());
        
        // Traitement des résultats
        for (KnowledgeBaseService.RuleMatch match : matches) {
        	// Une règle peut donner plusieurs dispenses (ex: Infra -> Systèmes + Réseaux)
            for (KbCorrespondenceRuleTarget target : match.rule().getTargets()) {
                UE ueCible = target.getUe();
                
                // Vérification anti-doublon
                // Si l'étudiant a déjà cette dispense (automatique ou manuelle), on ne l'ajoute pas 2 fois
                boolean exists = req.getItems().stream()
                        .anyMatch(i -> i.getUe().getCode().equals(ueCible.getCode()));

                if (!exists) {
                	// Création de la proposition automatique
                    ExemptionItem newItem = ExemptionItem.builder()
                            .request(req)
                            .ue(ueCible)
                            .decision(DecisionItem.AUTO_ACCEPTED) 		// C'est le système qui valide 
                            .totalEctsMatches(true)						// Par définition, si la règle KB passe, les crédits sont bons
                            .justifyingCourses(match.studentCourses())	// On lie les cours justificatifs
                            .build();

                    itemDao.save(newItem);
                    req.addItem(newItem);
                }
            }
        }
        
        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }

    // ————— SOUMISSION (CORRIGÉE) —————

    // Valide et verrouille la demande. 
    public ExemptionRequestFullDto submitRequest(UUID requestId) {
        // Récupération simple (sans check de draft immédiat)
        ExemptionRequest req = reqDao.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        // Gestion des statuts spécifiques
        if (req.getStatut() == StatutDemande.SUBMITTED) {
            throw new IllegalStateException("Cette demande a déjà été envoyée.");
        }
        
        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Impossible de soumettre : statut invalide (" + req.getStatut() + ")");
        }

        // Appel de la validation métier centralisée
        validateSubmission(req);

        // Si tout passe, on verrouille
        req.setStatut(StatutDemande.SUBMITTED);
        return mapper.toExemptionRequestFullDto(reqDao.save(req));
    }


    // ————— UTILITAIRES & VALIDATEURS —————

    // Helper pour les méthodes de MODIFICATION (add/delete).
    // Bloque tout si ce n'est pas un brouillon.
    private ExemptionRequest getDraftOrThrow(UUID id) {
        ExemptionRequest req = reqDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));
        
        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Modification impossible : demande non brouillon");
        }
        return req;
    }

    // Validateur CENTRALISÉ : Vérifie la cohérence complète du dossier.
    private void validateSubmission(ExemptionRequest req) {
        // Vérifier qu'il y a des cours
        if (req.getExternalCourses().isEmpty()) {
            throw new IllegalStateException("Votre dossier est vide. Veuillez ajouter des cours externes.");
        }

        // Vérifier qu'il y a des demandes de dispense (Items)
        if (req.getItems().isEmpty()) {
             throw new IllegalStateException("Aucune demande de dispense générée. Utilisez l'analyse automatique ou l'ajout manuel.");
        }

        // CHECK ORPHELINS
        // On récupère tous les IDs des cours encodés
        Set<UUID> allExternalIds = req.getExternalCourses().stream()
                .map(ExternalCourse::getId)
                .collect(Collectors.toSet());

        // On récupère tous les IDs des cours utilisés dans les items (via flatMap)
        Set<UUID> usedExternalIds = req.getItems().stream()
                .flatMap(item -> item.getJustifyingCourses().stream())
                .map(ExternalCourse::getId)
                .collect(Collectors.toSet());

        // On vérifie que used contient tout all
        if (!usedExternalIds.containsAll(allExternalIds)) {
            // On calcule ceux qui manquent pour aider l'utilisateur
            allExternalIds.removeAll(usedExternalIds);
            throw new IllegalStateException("Tous les cours externes doivent être liés à une demande de dispense. Cours non traités : " + allExternalIds.size());
        }

        // Validation des Documents (Preuves)
        boolean hasGlobalDoc = req.getGlobalDocuments().stream()
                .anyMatch(d -> d.getExternalCourse() == null);
        
        boolean allCoursesHaveProof = req.getExternalCourses().stream()
                .allMatch(c -> !c.getDocuments().isEmpty());

        if (!hasGlobalDoc && !allCoursesHaveProof) {
            throw new IllegalStateException(
                "Documents manquants : Fournissez soit un relevé de notes global, soit une preuve par cours."
            );
        }
    }

    // Récupérer la liste des requetes d'un etudiant 
    @Transactional(readOnly = true)
    public List<ExemptionRequestDto> myRequests(String email) {
        return reqDao.findAllByEtudiantEmail(email).stream()
                .map(mapper::toExemptionRequestDto)
                .toList();
    }
    
    // Récupérer le détail complet (avec cours et documents) pour l'affichage
    public ExemptionRequestFullDto getRequestDetail(UUID requestId) {
        ExemptionRequest req = reqDao.findWithAllById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));
        return mapper.toExemptionRequestFullDto(req);
    }
    
    public void deleteDraft(UUID requestId) {
        ExemptionRequest req = getDraftOrThrow(requestId);
        reqDao.delete(req);
    }
    
    public void deleteDocument(UUID docId) {
        SupportingDocument doc = docDao.findById(docId)
            .orElseThrow(() -> new IllegalArgumentException("Document introuvable"));
        
        // Vérification de sécurité : le dossier doit être en brouillon
        ExemptionRequest req = doc.getRequest() != null ? doc.getRequest() : doc.getExternalCourse().getRequest();
        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Impossible de supprimer : dossier verrouillé");
        }
        
        docDao.delete(doc);
    }
}