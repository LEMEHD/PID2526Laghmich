package org.isfce.pid.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateExemptionRequestDto {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String sectionCode;
}