package org.isfce.pid.dao;

import org.isfce.pid.model.KbCorrespondenceRule;
import org.isfce.pid.model.KbSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface IKbCorrespondenceRuleDao extends JpaRepository<KbCorrespondenceRule, UUID> {
    // Récupérer toutes les règles connues pour une école donnée
    List<KbCorrespondenceRule> findByEcole(KbSchool ecole);
}