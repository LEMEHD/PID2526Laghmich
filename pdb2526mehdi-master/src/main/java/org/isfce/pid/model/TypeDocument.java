package org.isfce.pid.model;

/**
 * Énumération définissant les catégories de documents justificatifs pouvant être téléversés.
 * Cette typologie permet de distinguer les documents globaux (concernant tout le dossier)
 * des documents spécifiques (concernant un cours particulier).
 */
public enum TypeDocument {


    BULLETIN, 		// Document généralement attaché au niveau global de la demande.

    PROGRAMME, 		// Document prouvant le contenu d'un cours externe spécifique.

    MOTIVATION,

    AUTRE

}