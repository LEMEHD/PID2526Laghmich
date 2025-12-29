package org.isfce.pid.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO (Data Transfer Object) utilisé pour l'ajout d'un document justificatif
 * spécifique à un cours externe (ex: Programme de cours, Table des matières).
 */
@Data
public class AddCourseDocumentDto {

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