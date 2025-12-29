package org.isfce.pid.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) "Léger" représentant une Unité d'Enseignement (UE).
 * Ce DTO contient les informations administratives et descriptives de base d'un cours,
 * mais exclut la liste détaillée des acquis d'apprentissage 
 * pour alléger les transferts de données (ex: affichage dans une liste).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UEDto {

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
    @Min(1)
    private int nbPeriodes;

    /**
     * Nombre de crédits ECTS.
     */
    @Min(1)
    private int ects;

    /**
     * Description textuelle du programme de cours (Contenu).
     */
    private String prgm;
}