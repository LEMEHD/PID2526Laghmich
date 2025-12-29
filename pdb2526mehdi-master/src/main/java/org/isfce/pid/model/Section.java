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

/**
 * Entité représentant une Section (ou filière) d'enseignement au sein de l'établissement.
 * Une section regroupe un ensemble d'Unités d'Enseignement (UE) qui constituent son programme.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "listeUE")
@Getter
@ToString(exclude = "listeUE")
@Entity(name = "TSECTION")
public class Section {

    /**
     * Code unique identifiant la section (Clé primaire).
     * Ex: "IG", "COMPTA".
     */
    @Id
    @Column(length = 10)
    private String code;

    /**
     * Libellé complet de la section.
     */
    @NotBlank
    @Column(length = 100, nullable = false)
    private String nom;

    /**
     * Liste des Unités d'Enseignement (UE) associées à cette section.
     * Relation Many-to-Many gérée via la table de jointure TSEC_UE.
     */
    @ManyToMany
    @JoinTable(
            name = "TSEC_UE",
            joinColumns = @JoinColumn(name = "FKSECTION"),
            inverseJoinColumns = @JoinColumn(name = "FKUE")
    )
    private Set<UE> listeUE = new HashSet<>();
}