package org.isfce.pid.dto;

import java.util.Set;
import java.util.UUID;

import org.isfce.pid.model.DecisionItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) représentant une ligne de dispense spécifique au sein d'un dossier.
 * Cet objet fait le lien entre une Unité d'Enseignement (UE) visée (le but)
 * et les cours externes présentés comme justificatifs (les preuves).
 * Il porte également l'état de la décision (Accepté, Refusé, etc.).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExemptionItemDto {

    /**
     * Identifiant unique technique de la ligne de dispense.
     */
    private UUID id;

    /**
     * L'Unité d'Enseignement (UE) interne pour laquelle la dispense est demandée.
     * Utilise le DTO léger {@link UEDto} pour fournir les informations essentielles (Code, Nom, ECTS).
     */
    private UEDto ue;

    /**
     * Indicateur calculé : Vrai si la somme des crédits ECTS des cours justificatifs
     * est supérieure ou égale au nombre de crédits de l'UE visée.
     */
    private boolean totalEctsMatches;

    /**
     * État actuel de la décision pour cette demande spécifique (ex: PENDING, ACCEPTED).
     */
    private DecisionItem decision;

    /**
     * Note attribuée (sur 20) par le professeur en cas d'acceptation.
     * Peut être null tant que la décision n'est pas finalisée.
     */
    private Integer noteAccordee;

    /**
     * Liste des cours externes (preuves) attachés à cette demande spécifique.
     */
    private Set<ExternalCourseDto> justifyingCourses;
}