package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Entity
@Table(name = "kb_correspondance_rule")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KbCorrespondenceRule extends BaseEntity {

    /**
     * École concernée par cette règle.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private KbSchool ecole;

    /**
     * Petite description pour l'admin :
     * ex. "ULB INFO-F101 → IPAP".
     */
    @Column(nullable = false)
    private String description;

    /**
     * ECTS minimum requis sur l'ensemble des cours sources (optionnel).
     * Peut être null si tu ne veux pas encore gérer cette logique.
     */
    private Integer minTotalEcts;

    @OneToMany(
        mappedBy = "rule",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private Set<KbCorrespondenceRuleSource> sources = new HashSet<>();

    @OneToMany(
        mappedBy = "rule",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private Set<KbCorrespondenceRuleTarget> targets = new HashSet<>();

    // Méthodes utilitaires pour gérer les deux côtés de la relation (optionnel mais pratique)

    public void addSource(KbCorrespondenceRuleSource src) {
        sources.add(src);
        src.setRule(this);
    }

    public void addTarget(KbCorrespondenceRuleTarget tgt) {
        targets.add(tgt);
        tgt.setRule(this);
    }
}
