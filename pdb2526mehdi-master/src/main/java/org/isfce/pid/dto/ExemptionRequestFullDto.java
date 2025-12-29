package org.isfce.pid.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO (Data Transfer Object) "Complet" pour une demande de dispense.
 * Étend {@link ExemptionRequestDto} pour inclure l'ensemble des données liées au dossier
 * (cours externes, documents justificatifs, lignes de dispense).
 * Ce DTO est "lourd" et doit être utilisé uniquement pour la consultation détaillée
 * d'un dossier spécifique, pas pour l'affichage des listes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Indispensable pour que le Builder fonctionne avec l'héritage
@EqualsAndHashCode(callSuper = true)
public class ExemptionRequestFullDto extends ExemptionRequestDto {

    /**
     * Liste complète des cours externes suivis (les "ingrédients").
     */
    private Set<ExternalCourseDto> externalCourses;

    /**
     * Liste des documents justificatifs globaux (Bulletin, Diplôme...).
     */
    private Set<SupportingDocumentDto> documents;

    /**
     * Liste des lignes de dispense (les demandes spécifiques "UE par UE").
     */
    private Set<ExemptionItemDto> items;
}