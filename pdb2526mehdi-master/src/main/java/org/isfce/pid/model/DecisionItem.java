package org.isfce.pid.model;

public enum DecisionItem {
	
	PENDING, 			// Tant que l'étudiant n'a pas soumis.
	AUTO_ACCEPTED, 		// Le système a reconnu la correspondance
	NEEDS_REVIEW, 		// Le système ne connait pas ou le score de l'IA est trop bas.
	ACCEPTED, 			// Validé manuellement par le prof/direction.
	REJECTED 			// Refusé.
	
}
