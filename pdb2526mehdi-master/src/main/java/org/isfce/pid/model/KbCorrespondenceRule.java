package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant une règle de correspondance dans la Base de Connaissances (Knowledge Base).
 * Une règle définit comment un ensemble de cours externes ("Sources") provenant d'une école spécifique
 * permet de valider automatiquement un ou plusieurs cours internes ("Targets"/UE).
 */
@Getter
@Setter
@Entity
@Table(name = "kb_correspondance_rule")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KbCorrespondenceRule extends BaseEntity {

    /**
     * L'établissement externe auquel cette règle s'applique.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private KbSchool ecole;

    /**
     * Description administrative de la règle (ex: "ULB INFO-F101 → IPAP").
     */
    @Column(nullable = false)
    private String description;

    /**
     * Nombre minimum de crédits ECTS cumulés requis parmi les cours sources pour valider la règle.
     * Permet de gérer les cas où un cours externe couvre la matière mais avec un volume horaire insuffisant.
     */
    private Integer minTotalEcts;

    /**
     * Liste des cours externes (ingrédients) nécessaires pour satisfaire cette règle.
     */
    @OneToMany(
            mappedBy = "rule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<KbCorrespondenceRuleSource> sources = new HashSet<>();

    /**
     * Liste des UEs internes (résultats) qui seront accordées si la règle est satisfaite.
     */
    @OneToMany(
            mappedBy = "rule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<KbCorrespondenceRuleTarget> targets = new HashSet<>();

    // ————— MÉTHODES UTILITAIRES (Helpers) —————

    /**
     * Ajoute une source (ingrédient) à la règle et maintient la cohérence bidirectionnelle.
     *
     * @param src La source à ajouter.
     */
    public void addSource(KbCorrespondenceRuleSource src) {
        sources.add(src);
        src.setRule(this);
    }

    /**
     * Ajoute une cible (résultat) à la règle et maintient la cohérence bidirectionnelle.
     *
     * @param tgt La cible à ajouter.
     */
    public void addTarget(KbCorrespondenceRuleTarget tgt) {
        targets.add(tgt);
        tgt.setRule(this);
    }
}