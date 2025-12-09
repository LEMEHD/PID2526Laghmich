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

    public SupportingDocument addDocument(UUID requestId, TypeDocument type, String url) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        SupportingDocument d = SupportingDocument.builder()
                .request(req)
                .type(type)
                .urlStockage(url)
                .build();

        return docDao.save(d);
    }

    /**
     * Permet à l'étudiant de déclarer manuellement une demande de dispense pour une UE spécifique.
     * (Étape 3 du flux : "Je veux être dispensé de IPAP")
     */
    public ExemptionItem addManualItem(UUID requestId, UE ueCible, Set<UUID> externalCourseIds) {
        ExemptionRequest req = getDraftOrThrow(requestId);

        // On récupère les cours externes déjà encodés par l'étudiant
        Set<ExternalCourse> selectedCourses = new HashSet<>(extCourseDao.findAllById(externalCourseIds));

        // Validation : les cours doivent appartenir à la demande
        if (!selectedCourses.stream().allMatch(c -> c.getRequest().getId().equals(requestId))) {
            throw new IllegalArgumentException("Certains cours sélectionnés n'appartiennent pas à cette demande");
        }

        ExemptionItem item = ExemptionItem.builder()
                .request(req)
                .ue(ueCible)
                .justifyingCourses(selectedCourses) // On lie les ingrédients au plat
                .decision(DecisionItem.PENDING)     // En attente de soumission
                .build();

        return itemDao.save(item);
    }

    // ————— LE CŒUR DU SYSTÈME : SOUMISSION (SUBMIT) —————

    public ExemptionRequestDto submit(UUID id) {
        ExemptionRequest req = reqDao.findWithAllById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (req.getStatut() != StatutDemande.DRAFT) {
            throw new IllegalStateException("Demande déjà soumise");
        }

        // 1. Validations minimales
        if (req.getExternalCourses().isEmpty()) throw new IllegalStateException("Ajoutez au moins un cours externe.");
        if (req.getDocuments().isEmpty()) throw new IllegalStateException("Ajoutez au moins un document justificatif.");

        // 2. Appel au Moteur de Règles (KnowledgeBaseService)
        // On récupère toutes les règles académiques qui matchent avec les cours de l'étudiant
        List<KbCorrespondenceRule> matchingRules = kbService.findMatchingRules(req.getExternalCourses());

        // 3. Application des règles (Auto-Acceptation)
        int autoAcceptedCount = 0;

        for (KbCorrespondenceRule rule : matchingRules) {
            for (KbCorrespondenceRuleTarget target : rule.getTargets()) {
                UE ueCible = target.getUe();

                // On cherche si l'étudiant a déjà créé un item pour cette UE
                Optional<ExemptionItem> existingItem = req.getItems().stream()
                        .filter(i -> i.getUe().equals(ueCible))
                        .findFirst();

                ExemptionItem itemToUpdate;

                if (existingItem.isPresent()) {
                    // Cas A : L'étudiant l'a demandé -> On la valide automatiquement !
                    itemToUpdate = existingItem.get();
                } else {
                    // Cas B : L'étudiant a oublié de demander cette dispense -> On la crée pour lui (Suggestion)
                    itemToUpdate = ExemptionItem.builder()
                            .request(req)
                            .ue(ueCible)
                            .decision(DecisionItem.PENDING) // On le crée, mais on settera le statut juste après
                            .build();
                    // On lie les cours sources de la règle à cet item
                    // Note: Il faudrait une petite logique ici pour retrouver les ExternalCourses correspondant aux Sources de la règle
                    // Pour simplifier ici, on laisse vide ou on fait un mapping inverse complexe
                    req.addItem(itemToUpdate);
                }

                // On applique la décision positive
                itemToUpdate.setDecision(DecisionItem.AUTO_ACCEPTED);
                itemToUpdate.setTotalEctsMatches(true);
                itemDao.save(itemToUpdate);
                autoAcceptedCount++;
            }
        }

        // 4. Mise à jour du statut global
        if (autoAcceptedCount > 0 && autoAcceptedCount == req.getItems().size()) {
             // Idéalement on pourrait dire "ACCEPTED" direct, mais souvent un prof veut vérifier quand même
             req.setStatut(StatutDemande.SUBMITTED); 
        } else {
             req.setStatut(StatutDemande.SUBMITTED);
        }
        
        req.setUpdatedAt(Instant.now());
        reqDao.save(req);

        return mapper.toExemptionRequestFullDto(req); // On renvoie le DTO final
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
                .map(mapper::toExemptionRequestDto) // <--- Correction ici
                .toList();
    }
    
    public void deleteRequest(UUID id) {
        ExemptionRequest req = getDraftOrThrow(id); // Vérifie aussi que c'est un DRAFT
        reqDao.delete(req);
    }
}