package org.isfce.pid.dto;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO (Data Transfer Object) de base représentant un cours externe.
 * Contient les informations descriptives du cours (Code, Libellé, ECTS)
 * ainsi que les documents qui lui sont directement attachés (Syllabus, Table des matières).
 * Sert de base pour {@link ExternalCourseFullDto} grâce à l'annotation {@code @SuperBuilder}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ExternalCourseDto {

    /**
     * Identifiant unique technique du cours externe.
     */
    private UUID id;

    /**
     * Établissement d'origine (ex: "ULB", "HE2B").
     */
    @NotBlank
    private String etablissement;

    /**
     * Code du cours dans l'établissement d'origine (ex: "INFO-H-401").
     */
    @NotBlank
    private String code;

    /**
     * Intitulé officiel du cours.
     */
    @NotBlank
    private String libelle;

    /**
     * Nombre de crédits ECTS obtenus.
     */
    @Min(1)
    private int ects;

    /**
     * URL vers le descriptif en ligne (facultatif).
     */
    private String urlProgramme;

    /**
     * Indicateur pour l'interface utilisateur (UI).
     * Permet d'afficher rapidement une icône (ex: trombone) dans les listes
     * sans devoir parser toute la liste des documents.
     */
    private boolean hasDocAttached;

    /**
     * Liste des documents justificatifs spécifiques à ce cours
     * (ex: PDF du plan de cours).
     */
    private Set<SupportingDocumentDto> programmes;
}