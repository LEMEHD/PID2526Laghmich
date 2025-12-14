package org.isfce.pid.service;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dao.IStudentDao;
import org.isfce.pid.model.Student;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final IStudentDao studentDao;

    /**
     * Récupère un étudiant par son email, ou le crée s'il n'existe pas encore.
     * Utile lors de la première connexion via Keycloak.
     */
    public Student getOrCreateByEmail(String email) {
        return studentDao.findByEmail(email)
                .orElseGet(() -> studentDao.save(
                        Student.builder()
                                .email(email)
                                .prenom("To define") // À récupérer du token OIDC si possible
                                .nom("To define")
                                .build()
                ));
    }
    
    public Optional<Student> findByEmail(String email) {
        return studentDao.findByEmail(email);
    }
}