package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant une "Source" (ou ingrédient) d'une règle de correspondance.
 * Elle lie une règle de correspondance à un cours externe spécifique de la
 * Base de Connaissances (Knowledge Base), indiquant que ce cours est requis
 * pour obtenir la dispense.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kb_rule_source")
public class KbCorrespondenceRuleSource extends BaseEntity {

    /**
     * La règle de correspondance parente à laquelle cette source appartient.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    @ToString.Exclude
    private KbCorrespondenceRule rule;

    /**
     * Le cours externe (référentiel KB) requis pour satisfaire cette condition.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cours_kb_id", nullable = false)
    private KbCourse cours;
}