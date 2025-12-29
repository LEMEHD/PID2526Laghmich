package org.isfce.pid.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * Représente une ligne de demande de dispense pour une Unité d'Enseignement (UE) spécifique.
 * Cette entité fait le lien entre une UE cible du programme et les cours externes
 * utilisés comme justificatifs (relation ManyToMany).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exemption_item")
public class ExemptionItem extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ExemptionRequest request;

    /**
     * L'Unité d'Enseignement (UE) pour laquelle la dispense est demandée.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ue_code")
    private UE ue;

    @Builder.Default
    @Column(nullable = false)
    private boolean totalEctsMatches = false;

    /**
     * État actuel de la décision (ex: EN_ATTENTE, ACCEPTE...).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DecisionItem decision = DecisionItem.PENDING;

    /**
     * Note attribuée par le professeur lors de la validation (sur 20).
     * Peut être null tant que la décision n'est pas finalisée.
     */
    @Min(value = 0, message = "{err.exemption.note.min}")
    @Max(value = 20, message = "{err.exemption.note.max}")
    @Column(nullable = true)
    private Integer noteAccordee;

    /**
     * Liste des cours externes utilisés pour justifier cette demande spécifique.
     */
    @ManyToMany
    @JoinTable(
            name = "item_course_link",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    private Set<ExternalCourse> justifyingCourses = new HashSet<>();

}