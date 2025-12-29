package org.isfce.pid.service;

import java.util.Optional;

import org.isfce.pid.dao.IStudentDao;
import org.isfce.pid.model.Student;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service métier gérant les profils étudiants.
 * Assure la synchronisation des identités (Onboarding) et l'accès aux données étudiantes.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimisation : Lecture seule par défaut
public class StudentService {

    private static final String DEFAULT_VALUE = "To define";

    private final IStudentDao studentDao;

    /**
     * Récupère un étudiant par son email ou le crée automatiquement s'il n'existe pas.
     * Cette méthode est critique pour le processus de première connexion (Onboarding),
     * assurant qu'un utilisateur authentifié possède toujours une entrée en base.
     *
     * @param email L'adresse email de l'étudiant.
     * @return L'entité Student persistée.
     */
    @Transactional // Surcharge explicite pour autoriser l'écriture
    public Student getOrCreateByEmail(String email) {
        return studentDao.findByEmail(email)
                .orElseGet(() -> createNewStudent(email));
    }

    /**
     * Recherche un étudiant par son email.
     *
     * @param email L'adresse email à rechercher.
     * @return Un Optional contenant l'étudiant s'il est trouvé.
     */
    public Optional<Student> findByEmail(String email) {
        return studentDao.findByEmail(email);
    }

    // ————— MÉTHODES PRIVÉES —————

    /**
     * Crée et sauvegarde un nouvel étudiant avec des valeurs par défaut.
     */
    private Student createNewStudent(String email) {
        Student newStudent = Student.builder()
                .email(email)
                .prenom(DEFAULT_VALUE)
                .nom(DEFAULT_VALUE)
                .build();
        return studentDao.save(newStudent);
    }
}