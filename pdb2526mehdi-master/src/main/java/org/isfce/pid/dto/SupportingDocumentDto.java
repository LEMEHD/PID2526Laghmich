package org.isfce.pid.dto;

import java.util.UUID;
import org.isfce.pid.model.TypeDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) représentant un document justificatif existant.
 * Utilisé pour afficher les informations d'un fichier déjà téléversé et stocké.
 * Diffère du {@link AddSupportingDocumentDto} car il contient l'ID technique
 * généré lors de la persistance.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportingDocumentDto {

    /**
     * Identifiant unique technique du document.
     */
    private UUID id;

    /**
     * Catégorie du document (ex: BULLETIN, PROGRAMME, MOTIVATION).
     */
    @NotNull
    private TypeDocument type;

    /**
     * Chemin ou URL d'accès au fichier (ex: lien S3 ou chemin relatif).
     * C'est ce lien qui sera utilisé par le frontend pour télécharger ou prévisualiser le fichier.
     */
    @NotBlank
    private String urlStockage;

    /**
     * Nom original du fichier (ex: "Mon_Bulletin_2023.pdf").
     * Utilisé pour l'affichage dans l'interface utilisateur, plus convivial que l'URL.
     */
    @NotBlank
    private String originalFileName;
}