package org.isfce.pid.dao;

import java.util.List;

import org.isfce.pid.dto.UEDto;
import org.isfce.pid.model.UE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité  UE (Unité d'Enseignement).
 * Gère le catalogue des cours internes de l'établissement.
 */
@Repository
public interface IUeDao extends JpaRepository<UE, String> {

    /**
     * Récupère la liste de toutes les UEs sous forme de DTO légers.
     * Utilise une projection JPQL pour instancier directement des UEDto.
     * Cette méthode est idéale pour les listes de sélection ou les catalogues simplifiés,
     * car elle évite de charger les relations lourdes (comme les acquis d'apprentissage).
     *
     * @return Une liste de DTOs contenant les métadonnées de base des UEs.
     */
    @Query("SELECT new org.isfce.pid.dto.UEDto(u.code, u.ref, u.nom, u.nbPeriodes, u.ects, u.prgm) FROM TUE u")
    List<UEDto> findAllAsDtos();
}