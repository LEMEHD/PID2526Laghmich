package org.isfce.pid.model;

/**
 * Énumération représentant les différents états possibles d'une demande de dispense
 * pour une Unité d'Enseignement (UE) spécifique.
 * Ces états permettent de suivre le cycle de vie de la décision, de la création
 * par l'étudiant jusqu'à la validation finale par le corps professoral.
 */
public enum DecisionItem {

    PENDING, 			// État initial lorsque l'étudiant constitue son dossier mais ne l'a pas encore soumis.

    AUTO_ACCEPTED, 		// Le système (Moteur de Règles) a identifié une correspondance exacte dans la Base de Connaissances et a validé la dispense sans intervention humaine.

    NEEDS_REVIEW, 		// Le système n'a pas trouvé de correspondance connue ou le score de confiance est trop bas. Une intervention manuelle d'un professeur est nécessaire pour statuer.

    ACCEPTED, 			// La dispense a été validée manuellement par un professeur ou la direction.

    REJECTED 			// La demande de dispense pour cette UE a été rejetée.

}