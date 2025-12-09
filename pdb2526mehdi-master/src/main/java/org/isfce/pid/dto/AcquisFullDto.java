package org.isfce.pid.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquisFullDto {
	@NotNull
	private Integer num;

	@NotBlank
	private String acquis;

	@Min(value = 1, message = "{err.aquis.pourcentage.min}")
	@Max(value = 100, message = "{err.aquis.pourcentage.max}")
	private Integer pourcentage;
}
