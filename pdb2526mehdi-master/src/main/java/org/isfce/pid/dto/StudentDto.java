package org.isfce.pid.dto;

import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) représentant un étudiant.
 * Contient les informations d'identité de base et le rattachement
 * à une section via son code (relation aplatie pour éviter d'embarquer l'objet Section complet).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {

    /**
     * Identifiant technique unique de l'étudiant en base de données.
     */
    private UUID id;

    /**
     * Adresse email (sert d'identifiant de connexion / Login).
     */
    @NotBlank
    @Email
    private String email;

    /**
     * Prénom de l'étudiant.
     */
    private String prenom;

    /**
     * Nom de famille de l'étudiant.
     */
    private String nom;

    /**
     * Code de la section académique (ex: "IG", "COMPTA").
     * Ce champ permet de lier l'étudiant à sa section sans nécessiter
     * le chargement de l'entité Section complète.
     */
    private String sectionCode;
}