package org.isfce.pid.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.isfce.pid.model.ExemptionRequest;
import org.isfce.pid.model.StatutDemande;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité ExemptionRequest.
 * Centralise les requêtes pour la gestion des dossiers de dispense.
 * Utilise intensivement EntityGraph pour optimiser le chargement des données
 * relationnelles et éviter le problème du "N+1 Selects".
 */
@Repository
public interface IExemptionRequestDao extends JpaRepository<ExemptionRequest, UUID> {

    /**
     * Récupère une demande par son ID en chargeant **toutes** les dépendances nécessaires
     * pour l'affichage complet (Detail View).
     * Le graphe charge :
     * Les cours externes globaux
     * Les documents globaux
     * Les lignes de dispense (items)
     * L'UE associée à chaque item
     * Les cours externes justifiant chaque item (Ajout conseillé)
     */
    @EntityGraph(attributePaths = {
        "externalCourses",
        "globalDocuments",
        "items",
        "items.ue",
        "items.justifyingCourses" // Important pour voir quelles preuves sont liées à quelle UE
    })
    Optional<ExemptionRequest> findWithAllById(UUID id);

    /**
     * Récupère toutes les demandes d'un étudiant via son email.
     * Optimisé pour le tableau de bord étudiant.
     * Charge les items pour permettre le calcul rapide de l'avancement (ex: 2 dispenses sur 5).
     */
    @EntityGraph(attributePaths = {"items"})
    List<ExemptionRequest> findAllByEtudiantEmail(String email);

    /**
     * Récupère les demandes filtrées par un statut spécifique.
     * Utile pour les filtres rapides (ex: "Voir tous les dossiers REJECTED").
     * Charge par défaut (Lazy) pour rester léger.
     */
    List<ExemptionRequest> findByStatut(StatutDemande statut);

    /**
     * Récupère toutes les demandes qui **ne sont pas** dans le statut indiqué.
     * Principalement utilisé par le dashboard Prof/Admin pour voir tout ce qui est
     * en attente de traitement (ex: {@code findByStatutNot(StatutDemande.DRAFT)}).
     */
    List<ExemptionRequest> findByStatutNot(StatutDemande statut);

}