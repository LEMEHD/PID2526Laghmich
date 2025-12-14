package org.isfce.pid.controller;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dao.ISectionDao;
import org.isfce.pid.dao.IUeDao;
import org.isfce.pid.dto.*;
import org.isfce.pid.model.Section;
import org.isfce.pid.model.TypeDocument;
import org.isfce.pid.model.UE;
import org.isfce.pid.service.ExemptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/exemptions")
@RequiredArgsConstructor
public class ExemptionControllerRest {

    private final ExemptionService exemptionService;
    
    // On injecte les DAOs directement pour transformer les Codes (String) en Entités
    private final ISectionDao sectionDao; 
    private final IUeDao ueDao;

    // ————— 0. DONNÉES DE RÉFÉRENCE (POUR LISTES DÉROULANTES) —————

    /**
     * Retourne la liste des sections disponibles pour le select "Nouvelle Demande".
     */
    @GetMapping("/sections")
    public ResponseEntity<List<Section>> getAllSections() {
        return ResponseEntity.ok(sectionDao.findAll());
    }
    
    // Note : Pour la liste des UE (addManualItem), utilise ton UEControllerRest existant (/api/ue/liste)

    // ————— 1. GESTION DES DEMANDES —————

    /**
     * Récupérer mes demandes (Dashboard étudiant)
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<ExemptionRequestDto>> getMyRequests(@RequestParam String email) {
        return ResponseEntity.ok(exemptionService.myRequests(email));
    }

    /**
     * Créer une nouvelle demande (Brouillon)
     * Le front envoie le code de la section choisie dans la liste déroulante.
     */
    @PostMapping("/create")
    public ResponseEntity<ExemptionRequestDto> createDraft(
            @RequestParam String email, 
            @RequestParam String sectionCode) { // On reçoit le code string du front
        
        // 1. On récupère l'objet Section
        Section section = sectionDao.findById(sectionCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section introuvable: " + sectionCode));
        
        // 2. On appelle le service avec l'objet
        // Note: Le service renvoie une entité, il faut idéalement la mapper en DTO.
        // Comme createDraft dans ton service renvoie 'ExemptionRequest', on va faire simple ici 
        // ou tu adaptes le service pour renvoyer un DTO. Ici je renvoie un OK simple pour l'instant.
        var req = exemptionService.createDraft(email, section);
        
        // Petite astuce : on construit un DTO minimaliste ou on appelle un mapper si injecté
        // Pour faire simple et respecter ton code actuel, je renvoie l'ID créé.
        return ResponseEntity.ok(ExemptionRequestDto.builder()
        		.id(req.getId())
        		.statut(req.getStatut())
        		.sectionCode(section.getCode())
        		.sectionNom(section.getNom())
        		.build());
    }
    
    // ————— 2. REMPLISSAGE DU SAC À DOS (Cours & Docs) —————

    @PostMapping("/{reqId}/add-course")
    public ResponseEntity<String> addExternalCourse(
            @PathVariable UUID reqId,
            @RequestBody ExternalCourseDto courseDto) {
        
        var created = exemptionService.addExternalCourse(
                reqId,
                courseDto.getEtablissement(),
                courseDto.getCode(),
                courseDto.getLibelle(),
                courseDto.getEcts(),
                courseDto.getUrlProgramme()
        );
        return ResponseEntity.ok(created.getId().toString());
    }

    /**
     * Ajouter un document GLOBAL (Bulletin) à la demande
     */
    @PostMapping("/{reqId}/add-document")
    public ResponseEntity<String> addGlobalDocument(
            @PathVariable UUID reqId,
            @RequestParam TypeDocument type,
            @RequestParam String url) {
        
        var doc = exemptionService.addGlobalDocument(reqId, type, url);
        return ResponseEntity.ok(doc.getId().toString());
    }

    /**
     * Ajouter un document SPÉCIFIQUE (Programme) à un cours externe
     */
    @PostMapping("/course/{courseId}/add-document")
    public ResponseEntity<String> addCourseDocument(
            @PathVariable UUID courseId,
            @RequestParam String url) {
        
        // On force le type PROGRAMME pour les documents liés aux cours
        var doc = exemptionService.addCourseDocument(courseId, url);
        return ResponseEntity.ok(doc.getId().toString());
    }

    // ————— 3. INTELLIGENCE & MATCHING —————

    /**
     * Action "Analyser mes cours" : Lance le moteur de règles
     * Renvoie le dossier complet mis à jour avec les propositions (AUTO_ACCEPTED)
     */
    @PostMapping("/{reqId}/analyze")
    public ResponseEntity<ExemptionRequestFullDto> analyzeRequest(@PathVariable UUID reqId) {
        return ResponseEntity.ok(exemptionService.analyzeRequest(reqId));
    }

    /**
     * Action "Ajout manuel" : Si l'étudiant veut forcer une demande pour une UE
     * Le front envoie le code de l'UE choisie dans la liste déroulante.
     */
    @PostMapping("/{reqId}/add-manual-item")
    public ResponseEntity<Void> addManualItem(
            @PathVariable UUID reqId,
            @RequestParam String ueCode,
            @RequestBody Set<UUID> externalCourseIds) {
        
        // 1. Récupération de l'objet UE via son DAO
        UE ue = ueDao.findById(ueCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UE introuvable: " + ueCode));
        
        // 2. Appel du service
        exemptionService.addManualItem(reqId, ue, externalCourseIds);
        
        return ResponseEntity.ok().build();
    }

    // ————— 4. FINALISATION —————

    /**
     * Soumission finale du dossier.
     * Vérifie les documents et change le statut.
     */
    @PostMapping("/{reqId}/submit")
    public ResponseEntity<ExemptionRequestFullDto> submitRequest(@PathVariable UUID reqId) {
        try {
            return ResponseEntity.ok(exemptionService.submit(reqId));
        } catch (IllegalStateException e) {
            // Renvoie une erreur 400 (Bad Request) si la validation échoue (ex: pas de documents)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    // Endpoint pour supprimer un brouillon (si l'étudiant veut recommencer)
    @DeleteMapping("/{reqId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID reqId) {
    	exemptionService.deleteRequest(reqId);
    	return ResponseEntity.ok().build();
    }
}