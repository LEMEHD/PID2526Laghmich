package org.isfce.pid.dao;

import java.util.Optional;
import java.util.UUID;

import org.isfce.pid.model.KbSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour les Écoles de la KB (Knowledge Base).
 * Gère la liste des établissements d'enseignement supérieur référencés par le système.
 * Ces écoles servent de point d'entrée pour définir l'origine des cours externes.
 */
@Repository
public interface IKbSchoolDao extends JpaRepository<KbSchool, UUID> {

    /**
     * Recherche un établissement par son code abréviatif (ex: "ULB", "ESI", "HE2B").
     * La recherche est insensible à la casse (Case Insensitive),
     * ce qui facilite la saisie utilisateur ou les correspondances automatiques
     * (ex: "ulb" ou "Ulb" trouvera bien l'enregistrement "ULB").
     *
     * @param code Le code ou l'abréviation de l'école.
     * @return L'école correspondante si elle existe.
     */
    Optional<KbSchool> findByCodeIgnoreCase(String code);

}