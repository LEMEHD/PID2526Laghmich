package org.isfce.pid.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO (Data Transfer Object) utilisé pour l'ajout d'un cours externe
 * (suivi et réussi dans un autre établissement) au dossier de dispense.
 */
@Data
public class AddExternalCourseDto {

    /**
     * Nom ou code de l'établissement d'origine (ex: "ULB", "HE2B").
     */
    @NotBlank
    private String etablissement;

    /**
     * Code unique du cours dans l'établissement d'origine (ex: "INFO-F101").
     */
    @NotBlank
    private String code;

    /**
     * Intitulé officiel du cours (ex: "Programmation Java").
     */
    @NotBlank
    private String libelle;

    /**
     * Nombre de crédits ECTS obtenus pour ce cours.
     */
    @Min(1)
    private int ects;

    /**
     * Lien vers le programme officiel ou le descriptif en ligne (facultatif).
     */
    private String urlProgramme;
}