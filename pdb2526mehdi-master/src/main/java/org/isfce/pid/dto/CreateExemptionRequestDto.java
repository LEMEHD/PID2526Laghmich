package org.isfce.pid.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO (Data Transfer Object) utilisé pour initialiser une nouvelle demande de dispense.
 * Ce DTO est requis lors de la création du dossier pour lier l'étudiant (via son email)
 * à une section académique spécifique.
 */
@Data
public class CreateExemptionRequestDto {

    /**
     * Adresse email de l'étudiant (identifiant unique).
     */
    @NotBlank
    @Email
    private String email;

    /**
     * Code de la section académique concernée par la demande (ex: "IG", "COMPTA").
     */
    @NotBlank
    private String sectionCode;
}