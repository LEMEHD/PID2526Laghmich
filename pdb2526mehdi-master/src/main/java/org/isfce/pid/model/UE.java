package org.isfce.pid.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder()
@EqualsAndHashCode(exclude = "acquis")
@Entity(name = "TUE")
@Getter
public class UE {
	@Id
	@Column(length = 20)
	private String code;
	@Column(unique = true, nullable = false, length = 20)
	private String ref;
	@Column(nullable = false, length = 50)
	private String nom;
	@Column(nullable = false)
	@Min(value = 1, message = "{err.ue.nbPeriodes}")
	private int nbPeriodes;
	@Column(nullable = false)
	@Min(value = 1, message = "{err.ue.nbECTS}")
	private int ects;
	@Lob
	@Column(nullable = false)
	private String prgm;
	/*
	 * Avec ce mapping, la clé étrangère FK_UE sera créée en base de données mais comme un acquis possède un id composé (@EmbeddedId avec le code de l'UE et un numéro), le code de l'UE
	 * apparaîtra 2 fois en BD.  
	 */
	@OneToMany(cascade = {CascadeType.MERGE}, orphanRemoval = true)
	@JoinColumn(name = "FK_UE", referencedColumnName = "code")
	@Builder.Default
	private List<Acquis> acquis = new ArrayList<>();
		
}
