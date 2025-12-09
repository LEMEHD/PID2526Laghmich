package org.isfce.pid.model;

public enum StatutDemande {
	
	DRAFT, 					// En cours de constitution
	SUBMITTED, 			 	// Envoyé, en attente que le moteur ou l'humain passe dessus.
	IN_REVIEW, 				// Si un prof a ouvert le dossier (utile pour éviter que 2 profs traitent le même dossier).
	PARTIALLY_ACCEPTED, 	// Au moins un item accepté, mais pas tout.
	ACCEPTED, 				// Tout est accepté.
	REJECTED 				// Tout est refusé.
	
}
