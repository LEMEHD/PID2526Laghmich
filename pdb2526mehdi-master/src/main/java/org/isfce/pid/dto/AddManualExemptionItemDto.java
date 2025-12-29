package org.isfce.pid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) utilisé pour soumettre manuellement une demande de dispense
 * pour une UE spécifique.
 * Ce DTO permet à l'étudiant de lier une Unité d'Enseignement (UE) cible du programme interne
 * à une ou plusieurs preuves (cours externes) qu'il a encodées.
 */
@Data
public class AddManualExemptionItemDto {

    /**
     * Le code unique de l'UE interne pour laquelle la dispense est demandée (ex: "IPAP").
     */
    @NotBlank
    private String ueCode;

    /**
     * Liste des identifiants (UUID) des cours externes utilisés comme justificatifs.
     * La liste ne peut pas être vide : il faut au moins un cours externe pour justifier la demande.
     */
    @NotEmpty
    private List<UUID> externalCourseIds;
}