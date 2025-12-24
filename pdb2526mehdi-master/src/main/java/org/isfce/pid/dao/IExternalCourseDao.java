package org.isfce.pid.dao;

import org.isfce.pid.model.ExternalCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface IExternalCourseDao extends JpaRepository<ExternalCourse, UUID> {
    
    // Vérifier si ce cours existe déjà dans la base (correspondance connue ?)
    // Ex: Est-ce qu'on connait "INFO-F-101" de "ULB" ?
    Optional<ExternalCourse> findByEtablissementAndCode(String etablissement, String code);
}