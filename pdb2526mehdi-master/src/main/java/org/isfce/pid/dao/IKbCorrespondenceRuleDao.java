package org.isfce.pid.dao;

import java.util.List;
import java.util.UUID;

import org.isfce.pid.model.KbCorrespondenceRule;
import org.isfce.pid.model.KbSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour la Base de Connaissances (Knowledge Base).
 * Gère les règles de correspondance historiques (ex: "Le cours Java de l'ULB vaut pour notre cours IPAP").
 * Permet au système de suggérer des décisions aux professeurs basées sur les précédents.
 */
@Repository
public interface IKbCorrespondenceRuleDao extends JpaRepository<KbCorrespondenceRule, UUID> {

    /**
     * Récupère toutes les règles d'équivalence connues pour un établissement spécifique.
     * Utile pour afficher une liste de suggestions lorsqu'un étudiant encode un cours venant
     * de cette école.
     *
     * @param ecole L'établissement scolaire source.
     * @return La liste des règles associées.
     */
    List<KbCorrespondenceRule> findByEcole(KbSchool ecole);

}