package org.isfce.pid.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity(name = "TACQUIS")
public class Acquis {

	@Embeddable // Record fonctionne à partir de Hibernate 6+
	public static record IdAcquis(
			String fkUE, Integer num) 
	implements Serializable {}
/*--------------------------------------------------------------*/
	// Clé primaire composée de (codeUE et num relatif) 
	//!FKUE sera la clé étrangère de la relation OneToMany (FK_UE doit être identique à codeUE lors d'un insert)
	@EmbeddedId
	private IdAcquis id;

	@NotBlank
	@Column(length = 500, nullable = false)
	private String acquis;

	@Min(value = 1, message = "{err.aquis.pourcentage.min}")
	@Max(value = 100, message = "{err.aquis.pourcentage.max}")
	@Column(nullable = false)
	private Integer pourcentage;
}

