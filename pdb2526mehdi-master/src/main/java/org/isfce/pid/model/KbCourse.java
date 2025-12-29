package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un cours externe référencé dans la Base de Connaissances (Knowledge Base).
 * Ces cours servent de références ("ingrédients") pour définir les règles de correspondance
 * et valider automatiquement les demandes de dispense.
 * Un cours est identifié de manière unique par le couple (École + Code).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "kb_course",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ecole_id", "code"})
)
public class KbCourse extends BaseEntity {

    /**
     * L'établissement scolaire dispensant ce cours.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private KbSchool ecole;

    /**
     * Code officiel du cours au sein de l'établissement externe (ex: "INFO-F101").
     */
    @Column(nullable = false, length = 64)
    private String code;

    /**
     * Libellé officiel du cours.
     */
    @Column(nullable = false)
    private String libelle;

    /**
     * Nombre de crédits ECTS associés.
     */
    @Column(nullable = false)
    private int ects;

    /**
     * URL vers la fiche descriptive officielle du cours (facultatif).
     */
    private String urlProgramme;
}