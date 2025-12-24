package org.isfce.pid.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.isfce.pid.model.ExemptionRequest;
import org.isfce.pid.model.StatutDemande;


public interface IExemptionRequestDao extends JpaRepository<ExemptionRequest, UUID> {
	/**
     * Récupère une demande par son ID en chargeant toutes les dépendances.
     * Pour la page "Détail de la demande".
     */
	@EntityGraph(attributePaths = {"externalCourses", "globalDocuments", "items", "items.ue"})
	Optional<ExemptionRequest> findWithAllById(UUID id);

    /**
     * Récupère toutes les demandes d'un étudiant via son email.
     * Pour le dashboard "Mon Espace Étudiant".
     */
    @EntityGraph(attributePaths = {"items"}) // On charge au moins les items pour montrer l'avancement
    List<ExemptionRequest> findAllByEtudiantEmail(String email);

    /**
     * Pour le dashboard Prof/Direction : Lister par statut.
     * Ici, on n'a peut-être pas besoin de tout charger (juste l'étudiant et la date suffisent pour la liste),
     * donc on n'utilise pas forcément le gros EntityGraph pour garder ça léger.
     */
    List<ExemptionRequest> findByStatut(StatutDemande statut);

    /**
     * Pour les profs : Tout ce qui n'est pas "Draft" (donc soumis ou en cours).
     */
    List<ExemptionRequest> findByStatutNot(StatutDemande statut);
}