package org.isfce.pid.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.isfce.pid.dto.AddCourseDocumentDto;
import org.isfce.pid.dto.AddExternalCourseDto;
import org.isfce.pid.dto.AddManualExemptionItemDto;
import org.isfce.pid.dto.AddSupportingDocumentDto;
import org.isfce.pid.dto.CreateExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestFullDto;
import org.isfce.pid.dto.ExternalCourseDto;
import org.isfce.pid.dto.SupportingDocumentDto;
import org.isfce.pid.service.ExemptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST principal pour la gestion des demandes de dispenses.
 * Ce contrôleur orchestre le flux de travail de l'étudiant : création du brouillon,
 * ajout des cours et preuves, analyse automatique via le moteur de règles,
 * et soumission finale du dossier.
 */
@RestController
@RequestMapping("/api/exemptions")
@RequiredArgsConstructor
public class ExemptionControllerRest {

    private final ExemptionService exemptionService;

    /**
     * Récupère la liste des demandes existantes pour un étudiant donné.
     *
     * @param email L'adresse email de l'étudiant.
     * @return Une liste de demandes simplifiées (DTO léger).
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<ExemptionRequestDto>> getMyRequests(@RequestParam("email") String email) {
        return ResponseEntity.ok(exemptionService.myRequests(email));
    }

    /**
     * Récupère le détail complet d'une demande spécifique.
     * Inclut les cours externes, les documents et les lignes de dispenses.
     *
     * @param reqId L'identifiant unique de la demande.
     * @return Le dossier complet (Full DTO).
     */
    @GetMapping("/{reqId}")
    public ResponseEntity<ExemptionRequestFullDto> getRequestDetail(@PathVariable("reqId") UUID reqId) {
        return ResponseEntity.ok(exemptionService.getRequestDetail(reqId));
    }

    /**
     * Initialise une nouvelle demande de dispense (statut BROUILLON).
     *
     * @param createDto Les informations initiales (Email + Code Section).
     * @return La demande créée avec son ID généré.
     */
    @PostMapping("/create")
    public ResponseEntity<ExemptionRequestDto> createDraft(@Valid @RequestBody CreateExemptionRequestDto createDto) {
        return ResponseEntity.ok(exemptionService.createDraft(createDto));
    }

    /**
     * Ajoute un cours externe (suivi dans un autre établissement) au dossier.
     *
     * @param reqId     L'identifiant de la demande en cours.
     * @param courseDto Les détails du cours réussi (Code, Libellé, ECTS, Etablissement).
     * @return Le cours externe créé.
     */
    @PostMapping("/{reqId}/add-course")
    public ResponseEntity<ExternalCourseDto> addExternalCourse(
            @PathVariable("reqId") UUID reqId,
            @Valid @RequestBody AddExternalCourseDto courseDto) {
        return ResponseEntity.ok(exemptionService.addExternalCourse(reqId, courseDto));
    }

    /**
     * Ajoute un document global au dossier (ex: Relevé de notes officiel, Diplôme).
     * Ce document est lié à la demande elle-même, pas à un cours spécifique.
     *
     * @param reqId  L'identifiant de la demande.
     * @param docDto Les métadonnées du document stocké.
     * @return Le document ajouté.
     */
    @PostMapping("/{reqId}/add-document")
    public ResponseEntity<SupportingDocumentDto> addGlobalDocument(
            @PathVariable("reqId") UUID reqId,
            @Valid @RequestBody AddSupportingDocumentDto docDto) {
        return ResponseEntity.ok(exemptionService.addGlobalDocument(reqId, docDto));
    }

    /**
     * Ajoute une preuve spécifique (Programme de cours) liée à un cours externe.
     *
     * @param courseId L'identifiant du cours externe concerné.
     * @param docDto   Les métadonnées du fichier (Type forcé à PROGRAMME).
     * @return Le document ajouté.
     */
    @PostMapping("/course/{courseId}/add-document")
    public ResponseEntity<SupportingDocumentDto> addCourseDocument(
            @PathVariable("courseId") UUID courseId,
            @Valid @RequestBody AddCourseDocumentDto docDto) {
        return ResponseEntity.ok(exemptionService.addCourseDocument(courseId, docDto));
    }

    /**
     * Déclenche l'analyse automatique du dossier par le moteur de règles.
     * Compare les cours encodés avec la base de connaissances pour proposer des dispenses.
     *
     * @param reqId L'identifiant de la demande à analyser.
     * @return Le dossier mis à jour avec les items générés (AUTO_ACCEPTED).
     */
    @PostMapping("/{reqId}/analyze")
    public ResponseEntity<ExemptionRequestFullDto> analyzeRequest(@PathVariable("reqId") UUID reqId) {
        return ResponseEntity.ok(exemptionService.analyzeRequest(reqId));
    }

    /**
     * Ajoute une demande de dispense manuelle.
     * Utilisé quand l'étudiant souhaite demander une dispense non détectée automatiquement.
     *
     * @param reqId L'identifiant de la demande.
     * @param dto   Les détails de la demande manuelle (UE visée + Cours justificatifs).
     * @return Le dossier mis à jour avec le nouvel item (PENDING).
     */
    @PostMapping("/{reqId}/add-manual-item")
    public ResponseEntity<ExemptionRequestFullDto> addManualItem(
            @PathVariable("reqId") UUID reqId,
            @Valid @RequestBody AddManualExemptionItemDto dto) {
        return ResponseEntity.ok(exemptionService.addManualItem(reqId, dto));
    }

    /**
     * Valide et soumet définitivement le dossier.
     * Vérifie la complétude (documents, orphelins) et verrouille le statut.
     *
     * @param reqId L'identifiant de la demande à soumettre.
     * @return Le dossier soumis (statut SUBMITTED).
     */
    @PostMapping("/{reqId}/submit")
    public ResponseEntity<ExemptionRequestFullDto> submitRequest(@PathVariable("reqId") UUID reqId) {
        return ResponseEntity.ok(exemptionService.submitRequest(reqId));
    }

    /**
     * Supprime intégralement un brouillon et toutes ses dépendances.
     *
     * @param reqId L'identifiant du brouillon à supprimer.
     * @return Une réponse vide (204 No Content).
     */
    @DeleteMapping("/{reqId}")
    public ResponseEntity<Void> deleteDraft(@PathVariable("reqId") UUID reqId) {
        exemptionService.deleteDraft(reqId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime un document justificatif.
     *
     * @param docId L'identifiant du document à supprimer.
     * @return Une réponse vide (204 No Content).
     */
    @DeleteMapping("/document/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("docId") UUID docId) {
        exemptionService.deleteDocument(docId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime une ligne de dispense spécifique (Item).
     *
     * @param itemId L'identifiant de la ligne à supprimer.
     * @return Le dossier complet mis à jour.
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<ExemptionRequestFullDto> deleteItem(@PathVariable("itemId") UUID itemId) {
        return ResponseEntity.ok(exemptionService.deleteItem(itemId));
    }
}