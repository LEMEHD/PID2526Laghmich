package org.isfce.pid.dto;

import java.time.Instant;
import java.util.UUID;

import org.isfce.pid.model.StatutDemande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO (Data Transfer Object) "Léger" représentant une demande de dispense.
 * Ce DTO contient les métadonnées essentielles (Qui ? Quoi ? Quand ? Statut ?)
 * mais exclut les collections lourdes (Documents, Cours externes, Items).
 * Il est principalement utilisé pour l'affichage des listes et tableaux de bord administratifs.
 * Il sert de classe parente pour {@link ExemptionRequestFullDto}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder // Nécessaire pour supporter l'héritage du Builder dans ExemptionRequestFullDto
public class ExemptionRequestDto {

    /**
     * Identifiant unique technique du dossier.
     */
    private UUID id;

    /**
     * Informations sur l'étudiant demandeur.
     */
    private StudentDto etudiant;

    /**
     * Code de la section académique (ex: "IG").
     */
    private String sectionCode;

    /**
     * Nom complet de la section (ex: "Informatique de Gestion").
     * Champ aplati (flattened) pour faciliter l'affichage direct dans les grilles
     * sans nécessiter de requête supplémentaire.
     */
    private String sectionNom;

    /**
     * État actuel du dossier.
     */
    private StatutDemande statut;

    /**
     * Date de création du dossier.
     */
    private Instant createdAt;

    /**
     * Date de la dernière modification.
     */
    private Instant updatedAt;
}