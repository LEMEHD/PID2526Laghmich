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
import lombok.Setter;

/**
 * Entité représentant une Unité d'Enseignement (UE).
 * Correspond aux cours officiels dispensés par l'établissement (le cursus interne).
 * C'est l'entité de référence ("Target") pour les demandes de dispense.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder()
@EqualsAndHashCode(exclude = "acquis")
@Entity(name = "TUE")
@Getter
@Setter
public class UE {

    /**
     * Code unique identifiant l'UE (Clé primaire).
     * Ex: "IPAP", "MATH1".
     */
    @Id
    @Column(length = 20)
    private String code;

    /**
     * Référence administrative unique.
     */
    @Column(unique = true, nullable = false, length = 20)
    private String ref;

    /**
     * Libellé complet de l'UE.
     */
    @Column(nullable = false, length = 50)
    private String nom;

    /**
     * Nombre de périodes de cours.
     */
    @Column(nullable = false)
    @Min(value = 1, message = "{err.ue.nbPeriodes}")
    private int nbPeriodes;

    /**
     * Nombre de crédits ECTS associés à l'UE.
     */
    @Column(nullable = false)
    @Min(value = 1, message = "{err.ue.nbECTS}")
    private int ects;

    /**
     * Description détaillée du programme de cours (Contenu).
     * Stocké sous forme de Large Object (LOB) pour accepter les textes longs.
     */
    @Lob
    @Column(nullable = false)
    private String prgm;

    /**
     * Liste des acquis d'apprentissage (Learning Outcomes) définis pour cette UE.
     * Note technique : La relation est mappée via la colonne "FK_UE".
     * En raison de la clé primaire composée dans l'entité {@link Acquis},
     * une redondance de la colonne de référence peut apparaître en base, ce qui est le comportement attendu.
     */
    @OneToMany(cascade = {CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "FK_UE", referencedColumnName = "code")
    @Builder.Default
    private List<Acquis> acquis = new ArrayList<>();

}