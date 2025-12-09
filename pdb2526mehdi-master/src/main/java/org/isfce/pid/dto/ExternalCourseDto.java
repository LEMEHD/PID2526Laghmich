package org.isfce.pid.dto;

import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCourseDto {
    private UUID id;
    
    @NotBlank
    private String etablissement;
    
    @NotBlank
    private String code;
    
    @NotBlank
    private String libelle;
    
    @Min(1)
    private int ects;
    
    private String urlProgramme;
}