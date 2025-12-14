package org.isfce.pid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "STUDENT", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Student extends BaseEntity {
	
	@NotBlank(message = "{err.student.email.blank}")
    @Email(message = "{err.email.invalid}")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "{err.student.firstname.blank}") 
    @Column(nullable = false, length = 50) 
    private String prenom;

    @NotBlank(message = "{err.student.lastname.blank}") 
    @Column(nullable = false, length = 50)
    private String nom;
    
    // Ajout conseillé suite à ma remarque précédente
    @ManyToOne
    @JoinColumn(name = "FK_SECTION")
    private Section section;
}