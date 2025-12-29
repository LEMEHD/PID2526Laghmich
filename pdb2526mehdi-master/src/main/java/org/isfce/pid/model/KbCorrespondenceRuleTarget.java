package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant une "Cible" (ou résultat) d'une règle de correspondance.
 * Elle définit l'Unité d'Enseignement (UE) du programme interne qui est accordée (dispensée)
 * lorsque les conditions de la règle parente sont remplies.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kb_rule_target")
public class KbCorrespondenceRuleTarget extends BaseEntity {

    /**
     * La règle de correspondance parente à laquelle cette cible appartient.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    @ToString.Exclude
    private KbCorrespondenceRule rule;

    /**
     * L'Unité d'Enseignement (UE) interne obtenue si la règle est satisfaite.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ue_id", nullable = false)
    private UE ue;
}