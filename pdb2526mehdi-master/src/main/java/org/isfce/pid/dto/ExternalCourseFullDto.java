package org.isfce.pid.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data; // <--- INDISPENSABLE
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Bonne pratique quand on étend une classe
public class ExternalCourseFullDto extends ExternalCourseDto {
	
    private List<SupportingDocumentDto> programmes; 

}