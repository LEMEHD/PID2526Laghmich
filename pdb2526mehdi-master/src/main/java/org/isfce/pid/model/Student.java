package org.isfce.pid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entité représentant un étudiant utilisateur de l'application.
 * L'étudiant est identifié de manière unique par son adresse email,
 * qui sert de clé de liaison avec le système d'authentification (SSO/Keycloak).
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "STUDENT", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Student extends BaseEntity {

    /**
     * Adresse email de l'étudiant.
     * Sert d'identifiant unique de connexion.
     */
    @NotBlank(message = "{err.student.email.blank}")
    @Email(message = "{err.email.invalid}")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Prénom de l'étudiant.
     */
    @NotBlank(message = "{err.student.firstname.blank}")
    @Column(nullable = false, length = 50)
    private String prenom;

    /**
     * Nom de famille de l'étudiant.
     */
    @NotBlank(message = "{err.student.lastname.blank}")
    @Column(nullable = false, length = 50)
    private String nom;

    /**
     * Section académique principale dans laquelle l'étudiant est inscrit.
     */
    @ManyToOne
    @JoinColumn(name = "FK_SECTION")
    private Section section;
}