package org.isfce.pid.dto;

import java.util.UUID;
import org.isfce.pid.model.TypeDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportingDocumentDto {
    private UUID id;
    
    @NotNull
    private TypeDocument type;
    
    @NotBlank
    private String urlStockage;
    
    @NotBlank
    private String originalFileName;
}