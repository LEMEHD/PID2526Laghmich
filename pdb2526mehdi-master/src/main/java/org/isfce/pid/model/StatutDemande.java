package org.isfce.pid.model;

/**
 * Énumération représentant le cycle de vie complet d'un dossier de demande de dispense.
 * Définit les étapes par lesquelles passe une demande ({@link ExemptionRequest}),
 * de sa constitution par l'étudiant jusqu'à la décision finale du corps professoral.
 */
public enum StatutDemande {

    DRAFT, 					// Le dossier est en cours de constitution par l'étudiant. Il n'est pas encore visible par l'administration et peut être modifié librement.

    SUBMITTED, 				// L'étudiant a finalisé et envoyé sa demande.Le dossier est verrouillé et en attente de traitement (automatique ou manuel).

    IN_REVIEW, 				// Un professeur ou un administrateur a ouvert le dossier pour analyse. Ce statut permet notamment d'éviter que deux personnes traitent le même dossier simultanément.

    PARTIALLY_ACCEPTED, 	// La décision finale a été rendue : certaines dispenses demandées ont été accordées, mais d'autres ont été refusées.

    ACCEPTED, 				// Toutes les dispenses demandées dans le dossier ont été accordées.

    REJECTED 				// L'intégralité de la demande de dispense a été rejetée.

}