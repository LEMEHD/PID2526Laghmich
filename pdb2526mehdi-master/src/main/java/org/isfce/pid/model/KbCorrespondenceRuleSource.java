package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kb_rule_source")
public class KbCorrespondenceRuleSource extends BaseEntity {

    /**
     * Règle à laquelle appartient cette source.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    @ToString.Exclude
    private KbCorrespondenceRule rule;

    /**
     * Cours externe (dans la base de connaissances) impliqué dans la règle.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cours_kb_id", nullable = false)
    private KbCourse cours;
}
