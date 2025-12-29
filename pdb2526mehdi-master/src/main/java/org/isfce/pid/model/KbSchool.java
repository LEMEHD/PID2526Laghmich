package org.isfce.pid.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entité représentant un établissement d'enseignement externe (École, Université).
 * Cette entité sert de référentiel racine pour la Base de Connaissances (KB).
 * Elle regroupe les cours externes et les règles de correspondance associées.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kb_school", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class KbSchool extends BaseEntity {

    /**
     * Code unique identifiant l'école (ex: "ULB", "HELB", "VINCI").
     * Ce code est utilisé comme clé métier pour les recherches et les imports.
     */
    @NotBlank
    @Column(nullable = false, length = 32)
    private String code;

    /**
     * Nom complet et officiel de l'établissement.
     */
    @NotBlank
    @Column(nullable = false)
    private String etablissement;

    /**
     * URL principale vers le catalogue de cours ou le site web de l'école.
     */
    private String urlProgramme;
}