package org.isfce.pid.dto;

import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private UUID id;
    
    @NotBlank
    @Email
    private String email;
    
    private String prenom;
    private String nom;
    private String sectionCode; // On renvoie juste le code pour lier facilement
}