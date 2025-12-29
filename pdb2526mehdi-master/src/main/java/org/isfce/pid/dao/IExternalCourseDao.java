package org.isfce.pid.dao;

import java.util.Optional;
import java.util.UUID;

import org.isfce.pid.model.ExternalCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité {@link ExternalCourse}.
 * Gère la persistance des cours suivis à l'extérieur (les "Preuves").
 */
@Repository
public interface IExternalCourseDao extends JpaRepository<ExternalCourse, UUID> {

    /**
     * Recherche un cours externe spécifique par son couple (Établissement, Code).
     * Attention : Si l'entité ExternalCourse est liée spécifiquement à un étudiant
     * (et non à un catalogue global partagé), cette méthode pourrait retourner un cours
     * encodé par un autre étudiant.
     * À utiliser avec prudence (ex: pour de l'auto-complétion ou des statistiques),
     * ou vérifier si le besoin n'est pas plutôt de chercher dans le dossier courant.
     *
     * @param etablissement Le nom de l'école (ex: "ULB").
     * @param code Le code du cours (ex: "INFO-F-101").
     * @return Le cours s'il existe déjà en base.
     */
    Optional<ExternalCourse> findByEtablissementAndCode(String etablissement, String code);

}