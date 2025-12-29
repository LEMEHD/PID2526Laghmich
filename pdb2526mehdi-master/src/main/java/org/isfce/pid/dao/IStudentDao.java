package org.isfce.pid.dao;

import java.util.Optional;
import java.util.UUID;

import org.isfce.pid.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité Student.
 * Gère les informations des étudiants (Demandeurs).
 */
@Repository
public interface IStudentDao extends JpaRepository<Student, UUID> {

    /**
     * Recherche un étudiant par son adresse email.
     * L'email étant unique et servant souvent d'identifiant de connexion (Login),
     * cette méthode est essentielle pour l'authentification ou pour retrouver
     * le dossier d'un étudiant spécifique.
     *
     * @param email L'adresse email exacte.
     * @return L'étudiant correspondant s'il existe.
     */
    Optional<Student> findByEmail(String email);

}