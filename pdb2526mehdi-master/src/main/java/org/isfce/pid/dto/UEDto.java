package org.isfce.pid.dto;

import lombok.Data;

@Data
public class UEDto {
	private String code;
    private String ref;
    private String nom;
    private int nbPeriodes;
    private int ects;
    private String prgm;
	public UEDto(String code, String ref, String nom, int nbPeriodes, int ects, String prgm) {
		super();
		this.code = code;
		this.ref = ref;
		this.nom = nom;
		this.nbPeriodes = nbPeriodes;
		this.ects = ects;
		this.prgm = prgm;
	}
    
}
