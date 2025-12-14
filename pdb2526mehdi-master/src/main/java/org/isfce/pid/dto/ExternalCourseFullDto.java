package org.isfce.pid.dto;

import java.util.Set; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExternalCourseFullDto extends ExternalCourseDto {
	
    
    private Set<SupportingDocumentDto> programmes; 

}