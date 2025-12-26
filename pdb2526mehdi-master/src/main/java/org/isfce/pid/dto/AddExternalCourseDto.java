package org.isfce.pid.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddExternalCourseDto {
    @NotBlank
    private String etablissement; // ex: "ULB"
    
    @NotBlank
    private String code;          // ex: "INFO-F101"
    
    @NotBlank
    private String libelle;       // ex: "Programmation Java"
    
    @Min(1)
    private int ects;             // ex: 5
    
    private String urlProgramme;  // ex: "https://..."
}