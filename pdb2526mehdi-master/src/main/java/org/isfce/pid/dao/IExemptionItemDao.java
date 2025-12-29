package org.isfce.pid.dao;

import java.util.UUID;

import org.isfce.pid.model.ExemptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité {@link ExemptionItem}.
 * Gère la persistance des lignes de dispense (le lien entre une UE visée et les cours externes).
 */
@Repository
public interface IExemptionItemDao extends JpaRepository<ExemptionItem, UUID> {


}