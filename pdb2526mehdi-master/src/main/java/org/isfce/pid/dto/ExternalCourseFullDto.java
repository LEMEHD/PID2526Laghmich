package org.isfce.pid.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ExternalCourseFullDto extends ExternalCourseDto {
	
    private List<SupportingDocumentDto> programmes; // La liste complète des PDF liés

}