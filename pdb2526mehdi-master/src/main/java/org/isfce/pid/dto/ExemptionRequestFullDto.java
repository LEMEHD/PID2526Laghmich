package org.isfce.pid.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Important pour l'héritage !
@EqualsAndHashCode(callSuper = true)
public class ExemptionRequestFullDto extends ExemptionRequestDto {
    
    private Set<ExternalCourseDto> externalCourses;
    private Set<SupportingDocumentDto> documents;
    private Set<ExemptionItemDto> items;
}