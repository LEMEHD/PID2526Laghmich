package org.isfce.pid.dao;

import java.util.List;

import org.isfce.pid.dto.SectionDto;
import org.isfce.pid.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité {@link Section}.
 * Gère les sections académiques (ex: "Informatique de Gestion").
 */
@Repository
public interface ISectionDao extends JpaRepository<Section, String> {

    /**
     * Récupère la liste de toutes les sections sous forme de DTO légers.
     * Utilise une Projection JPQL (Constructor Expression) pour instancier directement
     * les SectionDto depuis la requête.
     * Cela offre une excellente performance car on ne charge pas les entités complètes
     * (et donc on évite de charger potentiellement la liste des cours liés à chaque section).
     *
     * @return Une liste de DTOs contenant uniquement le code et le nom.
     */
    @Query("SELECT new org.isfce.pid.dto.SectionDto(s.code, s.nom) FROM TSECTION s")
    List<SectionDto> findAllAsDtos();

}