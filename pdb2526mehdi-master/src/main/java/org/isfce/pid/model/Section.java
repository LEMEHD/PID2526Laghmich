package org.isfce.pid.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "listeUE")
@Getter
@ToString(exclude = "listeUE")
@Entity(name = "TSECTION")
public class Section {
	@Id
	@Column(length = 10)
	private String code;
	@NotBlank
	@Column(length = 100, nullable = false)
	private String nom;
	@ManyToMany
	@JoinTable(name = "TSEC_UE", joinColumns = @JoinColumn(name = "FKSECTION"), 
	           inverseJoinColumns = @JoinColumn(name = "FKUE"))
	private Set<UE> listeUE = new HashSet<>();
}
