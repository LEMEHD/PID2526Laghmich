package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kb_rule_target")
public class KbCorrespondenceRuleTarget extends BaseEntity {

    /**
     * Règle à laquelle appartient cette cible.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    @ToString.Exclude
    private KbCorrespondenceRule rule;

    /**
     * UE ISFCE obtenue si la règle est satisfaite.
     * MODIFICATION : On pointe vers 'UE' (l'entité riche du Gradle) et non 'UEIsfce'.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ue_id", nullable = false)
    private UE ue;
}