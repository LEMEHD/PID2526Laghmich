package org.isfce.pid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entité représentant un document justificatif (Preuve).
 * Un document peut être rattaché soit globalement à la demande (ex: Relevé de notes, Diplôme),
 * soit spécifiquement à un cours externe (ex: Programme détaillé du cours).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "supporting_document")
public class SupportingDocument extends BaseEntity {

    /**
     * La demande de dispense globale à laquelle ce document est rattaché.
     * Ce champ est renseigné si le document concerne l'ensemble du dossier (ex: Bulletin).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ExemptionRequest request;

    /**
     * Le cours externe spécifique auquel ce document est rattaché.
     * Ce champ est renseigné si le document prouve le contenu d'un cours précis (ex: Table des matières).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_course_id")
    private ExternalCourse externalCourse;

    /**
     * Catégorie du document (ex: BULLETIN, DIPLOME, PROGRAMME).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeDocument type;

    /**
     * Chemin d'accès ou URL vers le fichier stocké physiquement.
     */
    @NotBlank
    @Column(nullable = false)
    private String urlStockage;

    /**
     * Nom original du fichier tel que téléchargé par l'utilisateur.
     */
    @Column(name = "original_file_name")
    private String originalFileName;

}