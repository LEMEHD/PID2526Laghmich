package org.isfce.pid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.isfce.pid.model.TypeDocument;

@Data
public class AddSupportingDocumentDto {
    @NotNull
    private TypeDocument type;   // BULLETIN, MOTIVATION, etc.
    
    @NotBlank
    private String urlStockage;  
    
    @NotBlank
    private String originalFileName;
}