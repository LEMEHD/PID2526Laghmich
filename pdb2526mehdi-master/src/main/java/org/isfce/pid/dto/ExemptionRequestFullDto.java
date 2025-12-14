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
    
}