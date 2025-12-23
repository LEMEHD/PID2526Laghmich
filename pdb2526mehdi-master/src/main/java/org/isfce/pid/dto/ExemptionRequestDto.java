package org.isfce.pid.dto;

import java.time.Instant;
import java.util.UUID;

import org.isfce.pid.model.StatutDemande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder // Au lieu de @Builder pour supporter l'héritage
public class ExemptionRequestDto {
    private UUID id;
    private StudentDto etudiant;
    
    private String sectionCode; 
    private String sectionNom;  
    
    private StatutDemande statut;
    private Instant createdAt;
    private Instant updatedAt;
}