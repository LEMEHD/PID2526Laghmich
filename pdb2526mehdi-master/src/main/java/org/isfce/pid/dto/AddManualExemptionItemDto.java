package org.isfce.pid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddManualExemptionItemDto {
    @NotBlank
    private String ueCode; // Le code du cours ISFCE (ex: "IPAP")
    
    @NotEmpty
    private List<UUID> externalCourseIds; // Les preuves (ex: ID du cours "Java ULB")
}