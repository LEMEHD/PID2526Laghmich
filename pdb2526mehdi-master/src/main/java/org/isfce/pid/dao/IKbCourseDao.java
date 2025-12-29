package org.isfce.pid.dao;

import java.util.Optional;
import java.util.UUID;

import org.isfce.pid.model.KbCourse;
import org.isfce.pid.model.KbSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour les cours de référence (KB).
 * Gère les cours "connus" du système (Knowledge Base), indépendamment des dossiers étudiants.
 * Ces cours servent de base pour les règles de correspondance.
 */
@Repository
public interface IKbCourseDao extends JpaRepository<KbCourse, UUID> {

    /**
     * Recherche un cours de référence spécifique dans une école donnée.
     * La recherche sur le code du cours est insensible à la casse (Case Insensitive),
     * ce qui permet de trouver "INFO-F-101" même si on cherche "info-f-101".
     *
     * @param ecole L'école de référence.
     * @param code Le code du cours (ex: "LINFO123").
     * @return Le cours de la Knowledge Base s'il existe.
     */
    Optional<KbCourse> findByEcoleAndCodeIgnoreCase(KbSchool ecole, String code);

}