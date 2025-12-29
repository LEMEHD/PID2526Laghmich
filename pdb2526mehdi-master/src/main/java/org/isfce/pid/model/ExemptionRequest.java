package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entité racine représentant un dossier de demande de dispense.
 * Elle agrège toutes les informations fournies par l'étudiant (cours externes, documents)
 * ainsi que les demandes spécifiques de dispense (Items) générées manuellement ou automatiquement.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exemption_request")
public class ExemptionRequest extends BaseEntity {

    /**
     * L'étudiant à l'origine de la demande.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    private Student etudiant;

    /**
     * La section (filière) dans laquelle l'étudiant est inscrit.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "section_code")
    private Section section;

    /**
     * Statut actuel du dossier (Brouillon, Soumis, En traitement, Clôturé).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutDemande statut = StatutDemande.DRAFT;

    /**
     * Liste des cours suivis et réussis dans un autre établissement.
     */
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<ExternalCourse> externalCourses = new HashSet<>();

    /**
     * Documents justificatifs globaux (ex: Relevé de notes officiel, Diplôme).
     * Ne contient pas les programmes de cours spécifiques (liés à {@link ExternalCourse}).
     */
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<SupportingDocument> globalDocuments = new HashSet<>();

    /**
     * Liste des lignes de dispense (Items) composant la demande.
     * Chaque item correspond à une UE du programme interne.
     */
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<ExemptionItem> items = new HashSet<>();

    // ————— MÉTHODES UTILITAIRES (Helpers) —————

    /**
     * Ajoute un cours externe et maintient la cohérence de la relation bidirectionnelle.
     */
    public void addExternalCourse(ExternalCourse c) {
        c.setRequest(this);
        externalCourses.add(c);
    }

    /**
     * Ajoute un document global et maintient la cohérence de la relation bidirectionnelle.
     */
    public void addDocument(SupportingDocument d) {
        d.setRequest(this);
        globalDocuments.add(d);
    }

    /**
     * Ajoute une ligne de dispense et maintient la cohérence de la relation bidirectionnelle.
     */
    public void addItem(ExemptionItem i) {
        i.setRequest(this);
        items.add(i);
    }
}