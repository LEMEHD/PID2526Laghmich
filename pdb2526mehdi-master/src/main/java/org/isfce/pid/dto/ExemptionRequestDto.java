package org.isfce.pid.dto;

import java.time.Instant;
import java.util.UUID;

import org.isfce.pid.model.StatutDemande;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder //
public class ExemptionRequestDto {
    private UUID id;
    private StudentDto etudiant;
    
    private String sectionCode; 
    private String sectionNom;  
    
    private StatutDemande statut;
    private Instant createdAt;
    private Instant updatedAt;
}