package org.isfce.pid.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) représentant un Acquis d'Apprentissage
 * dans une structure hiérarchique (imbriqué dans une UE).
 * Contrairement à {@link AcquisDto}, ce DTO ne contient pas la clé étrangère
 * vers l'UE parente, car il est destiné à être transporté à l'intérieur
 * d'un {@link UEFullDto}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class AcquisFullDto {

    /**
     * Numéro d'ordre de l'acquis au sein de l'UE.
     */
    @NotNull
    private Integer num;

    /**
     * Description textuelle de l'acquis.
     */
    @NotBlank
    private String acquis;

    /**
     * Poids de l'acquis dans l'évaluation (pourcentage de 1 à 100).
     */
    @Min(value = 1, message = "{err.aquis.pourcentage.min}")
    @Max(value = 100, message = "{err.aquis.pourcentage.max}")
    private Integer pourcentage;
}