package org.isfce.pid.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) représentant un Acquis d'Apprentissage.
 * Ce format "plat" est utilisé pour les opérations de transfert où la relation
 * avec l'UE parente est explicitée par la clé étrangère {@code fkUE},
 * plutôt que par une structure d'objet imbriquée.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquisDto {

    /**
     * Code de l'Unité d'Enseignement (UE) à laquelle cet acquis appartient.
     */
    @NotBlank
    private String fkUE;

    /**
     * Numéro d'ordre séquentiel de l'acquis au sein de l'UE.
     */
    @NotNull
    private Integer num;

    /**
     * Description textuelle du contenu de l'acquis.
     */
    @NotBlank
    private String acquis;

    /**
     * Poids de l'acquis dans l'évaluation finale (pourcentage de 1 à 100).
     */
    @Min(value = 1, message = "{err.aquis.pourcentage.min}")
    @Max(value = 100, message = "{err.aquis.pourcentage.max}")
    private Integer pourcentage;
}