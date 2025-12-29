package org.isfce.pid.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Représente un cours suivi et réussi par l'étudiant dans un établissement externe.
 * Ce cours sert de "brique de base" (ingrédient) pour justifier une ou plusieurs
 * demandes de dispense (ExemptionItem).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "external_course", indexes = @Index(columnList = "etablissement,code"))
public class ExternalCourse extends BaseEntity {

    /**
     * La demande de dispense globale à laquelle ce cours est rattaché.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ExemptionRequest request;

    /**
     * Code ou nom de l'établissement d'origine (ex: "ULB", "HE2B").
     */
    @NotBlank
    @Column(nullable = false, length = 100)
    private String etablissement;

    /**
     * Code identifiant le cours dans l'établissement d'origine (ex: "INFO-H-401").
     */
    @NotBlank
    @Column(nullable = false, length = 32)
    private String code;

    /**
     * Titre officiel du cours.
     */
    @NotBlank
    @Column(nullable = false)
    private String libelle;

    /**
     * Nombre de crédits ECTS associés à ce cours.
     */
    @Min(value = 1, message = "{err.externalcourse.ects.min}")
    @Column(nullable = false)
    private int ects;

    /**
     * URL vers le programme officiel ou le descriptif du cours (facultatif).
     */
    private String urlProgramme;

    /**
     * Documents justificatifs spécifiques à ce cours (ex: le PDF du plan de cours).
     */
    @Builder.Default
    @OneToMany(mappedBy = "externalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupportingDocument> documents = new HashSet<>();
}