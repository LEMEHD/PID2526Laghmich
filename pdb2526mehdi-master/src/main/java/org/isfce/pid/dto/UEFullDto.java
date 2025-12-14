package org.isfce.pid.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UEFullDto {
	@NotBlank
	private String code;
	@NotBlank
	private String ref;
	@NotBlank
	private String nom;
	@Min(value = 1, message = "{err.ue.nbPeriodes}")
	private int nbPeriodes;
	@Min(value = 1, message = "{err.ue.nbECTS}")
	private int ects;
	@NotBlank
	private String prgm;
	@Size(min = 1, message = "{err.ue.acquis}")
	@NotEmpty // acquis sans FKUE
	@Valid
	private List<AcquisFullDto> acquis;

}
