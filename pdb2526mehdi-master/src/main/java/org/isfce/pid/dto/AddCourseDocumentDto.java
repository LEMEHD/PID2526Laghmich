package org.isfce.pid.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCourseDocumentDto {
    @NotBlank
    private String urlStockage; // Lien vers le syllabus/programme
    
    @NotBlank
    private String originalFileName;
}