package org.isfce.pid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) représentant une Section (Filière d'études).
 * Objet léger utilisé principalement pour l'affichage (ex: titre du dossier)
 * ou pour alimenter des listes de choix (Dropdowns) dans l'interface.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDto {

    /**
     * Code unique identifiant la section (ex: "IG", "COMPTA").
     */
    private String code;

    /**
     * Libellé complet et lisible de la section (ex: "Informatique de Gestion").
     */
    private String nom;
}