package org.isfce.pid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.isfce.pid.model.TypeDocument;

/**
 * DTO (Data Transfer Object) utilisé pour l'ajout d'un document justificatif global
 * au dossier de dispense (ex: Relevé de notes, Diplôme, Lettre de motivation).
 * Contrairement à {@link AddCourseDocumentDto}, ce document n'est pas lié
 * à un cours externe spécifique mais concerne l'ensemble de la demande.
 */
@Data
public class AddSupportingDocumentDto {

    /**
     * Catégorie du document (ex: BULLETIN, MOTIVATION).
     */
    @NotNull
    private TypeDocument type;

    /**
     * Chemin de stockage ou URL d'accès au fichier téléversé.
     */
    @NotBlank
    private String urlStockage;

    /**
     * Nom original du fichier tel que fourni par l'utilisateur lors de l'upload.
     */
    @NotBlank
    private String originalFileName;
}