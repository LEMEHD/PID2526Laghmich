package org.isfce.pid.dto;

import java.util.Set;
import java.util.UUID;

import org.isfce.pid.model.DecisionItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExemptionItemDto {
    private UUID id;
    
    // On réutilise le DTO existant pour afficher les infos de l'UE cible
    private UEDto ue; 
    
    private boolean totalEctsMatches;
    private DecisionItem decision;
    private Integer noteAccordee;
    
    // Liste des cours externes qui justifient cet item
    private Set<ExternalCourseDto> justifyingCourses;
}