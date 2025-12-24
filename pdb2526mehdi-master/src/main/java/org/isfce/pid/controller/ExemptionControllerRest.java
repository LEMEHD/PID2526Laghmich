package org.isfce.pid.controller;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dto.*;
import org.isfce.pid.service.ExemptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exemptions")
@RequiredArgsConstructor
public class ExemptionControllerRest {

    private final ExemptionService exemptionService;

    // ————— 1. GESTION DES DEMANDES —————

    /**
     * Récupérer la liste des demandes 
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<ExemptionRequestDto>> getMyRequests(
            @RequestParam("email") String email) { 
        return ResponseEntity.ok(exemptionService.myRequests(email));
    }
    
    /**
     * Récupérer les détails d'une demande
     */
    @GetMapping("/{reqId}")
    public ResponseEntity<ExemptionRequestFullDto> getRequestDetail(@PathVariable("reqId") UUID reqId) {
        return ResponseEntity.ok(exemptionService.getRequestDetail(reqId));
    }

    /**
     * Créer une nouvelle demande (Brouillon)
     * Le front envoie le CODE de la section choisie dans la liste déroulante.
     * Body: { "email": "...", "sectionCode": "..." }
     */
    @PostMapping("/create")
    public ResponseEntity<ExemptionRequestDto> createDraft(
            @Valid @RequestBody CreateExemptionRequestDto createDto) { // @Valid active les vérifs (@NotBlank...)
        
        // Le contrôleur ne fait que passer la commande
        return ResponseEntity.ok(exemptionService.createDraft(createDto));
    }
    
    // ————— 2. REMPLISSAGE DU SAC À DOS (Cours & Docs) —————

    /**
     * Ajoute un cours externe réussi à la demande.
     */
    @PostMapping("/{reqId}/add-course")
    public ResponseEntity<ExternalCourseDto> addExternalCourse(
            @PathVariable("reqId") UUID reqId,
            @Valid @RequestBody AddExternalCourseDto courseDto) {
        
        return ResponseEntity.ok(exemptionService.addExternalCourse(reqId, courseDto));
    }

    /**
     * Ajouter un document GLOBAL (Bulletin, Diplôme...) à la demande
     */
    @PostMapping("/{reqId}/add-document")
    public ResponseEntity<SupportingDocumentDto> addGlobalDocument(
            @PathVariable("reqId") UUID reqId,
            @Valid @RequestBody AddSupportingDocumentDto docDto) {
        
        return ResponseEntity.ok(exemptionService.addGlobalDocument(reqId, docDto));
    }

    /**
     * Ajouter un document SPÉCIFIQUE (Programme) à un cours externe
     * Le type est automatiquement défini comme PROGRAMME.
     */
    @PostMapping("/course/{courseId}/add-document")
    public ResponseEntity<SupportingDocumentDto> addCourseDocument(
            @PathVariable("courseId") UUID courseId,
            @Valid @RequestBody AddCourseDocumentDto docDto) {
        
        return ResponseEntity.ok(exemptionService.addCourseDocument(courseId, docDto));
    }

    // ————— 3. INTELLIGENCE & MATCHING —————

    /**
     * Action "Analyser" : Lance le moteur de règles sur les cours encodés.
     * Renvoie le dossier complet mis à jour avec les propositions (AUTO_ACCEPTED).
     */
    @PostMapping("/{reqId}/analyze")
    public ResponseEntity<ExemptionRequestFullDto> analyzeRequest(
            @PathVariable("reqId") UUID reqId) {
        
        return ResponseEntity.ok(exemptionService.analyzeRequest(reqId));
    }

    /**
     * Ajoute une demande manuelle (statut PENDING).
     * L'étudiant dit : "Je veux être dispensé de l'UE X grâce à mes cours Y et Z".
     * URL: POST /api/exemptions/{reqId}/add-manual-item
     */
    @PostMapping("/{reqId}/add-manual-item")
    public ResponseEntity<ExemptionRequestFullDto> addManualItem(
            @PathVariable("reqId") UUID reqId,
            @Valid @RequestBody AddManualExemptionItemDto dto) {
        
        return ResponseEntity.ok(exemptionService.addManualItem(reqId, dto));
    }

    // ————— 4. FINALISATION —————

    /**
     * Soumission finale du dossier.
     * Vérifie les documents et change le statut.
     */
    @PostMapping("/{reqId}/submit")
    public ResponseEntity<ExemptionRequestFullDto> submitRequest(
            @PathVariable("reqId") UUID reqId) {
        
        return ResponseEntity.ok(exemptionService.submitRequest(reqId));
    }
    
    /**
     * Supprime un brouillon et tout son contenu.
     */
    @DeleteMapping("/{reqId}")
    public ResponseEntity<Void> deleteDraft(@PathVariable("reqId") UUID reqId) {
        exemptionService.deleteDraft(reqId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Supprime un document.
     */
    @DeleteMapping("/document/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID docId) {
        exemptionService.deleteDocument(docId);
        return ResponseEntity.noContent().build();
    }
}