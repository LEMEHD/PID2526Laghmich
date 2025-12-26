package org.isfce.pid.dto;

import java.util.Set; // <--- Vérifiez cet import
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder 
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
    
    private boolean hasDocAttached;

    private Set<SupportingDocumentDto> programmes; 
}