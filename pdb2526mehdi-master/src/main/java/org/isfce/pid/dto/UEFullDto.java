package org.isfce.pid.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) "Complet" représentant une Unité d'Enseignement (UE).
 * Contrairement à {@link UEDto}, ce DTO inclut la liste complète des acquis d'apprentissage.
 * Il est destiné à l'affichage détaillé d'un cours (Detail View) ou à l'édition,
 * où la connaissance du contenu pédagogique détaillé est nécessaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UEFullDto {

    /**
     * Code unique identifiant l'UE (ex: "IPAP").
     */
    @NotBlank
    private String code;

    /**
     * Référence administrative unique.
     */
    @NotBlank
    private String ref;

    /**
     * Libellé complet de l'UE.
     */
    @NotBlank
    private String nom;

    /**
     * Nombre de périodes de cours.
     */
    @Min(value = 1, message = "{err.ue.nbPeriodes}")
    private int nbPeriodes;

    /**
     * Nombre de crédits ECTS.
     */
    @Min(value = 1, message = "{err.ue.nbECTS}")
    private int ects;

    /**
     * Description détaillée du programme (Syllabus).
     */
    @NotBlank
    private String prgm;

    /**
     * Liste des acquis d'apprentissage (Learning Outcomes).
     * Utilise {@link AcquisFullDto} qui ne contient pas la clé étrangère vers l'UE,
     * puisque ces acquis sont contextuellement liés à l'UE parente ici présente.
     */
    @NotEmpty(message = "{err.ue.acquis}")
    @Valid // Important pour valider le contenu de chaque AcquisFullDto dans la liste
    private List<AcquisFullDto> acquis;

}