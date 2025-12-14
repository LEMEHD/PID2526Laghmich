package org.isfce.pid.dto;

import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExemptionRequestFullDto extends ExemptionRequestDto {
    
    private Set<ExternalCourseDto> externalCourses;
    private Set<SupportingDocumentDto> documents;
    private Set<ExemptionItemDto> items;
    
    // Constructeur pratique pour tout initialiser
    public ExemptionRequestFullDto(ExemptionRequestDto base, 
                                   Set<ExternalCourseDto> externalCourses,
                                   Set<SupportingDocumentDto> documents,
                                   Set<ExemptionItemDto> items) {
        super(base.getId(), base.getEtudiant(), base.getSectionCode(), base.getSectionNom(), 
              base.getStatut(), base.getCreatedAt(), base.getUpdatedAt());
        this.externalCourses = externalCourses;
        this.documents = documents;
        this.items = items;
    }
}