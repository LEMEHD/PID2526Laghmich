package org.isfce.pid.dao;

import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.Acquis.IdAcquis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité Acquis.
 * Gère la persistance des acquis d'apprentissage.
 * La clé primaire étant composée, le repository est typé avec IdAcquis.
 */
@Repository
public interface IAcquisDao extends JpaRepository<Acquis, IdAcquis> {

    /**
     * Compte le nombre d'acquis existants pour une UE spécifique.
     * Note technique Spring Data :
     * La syntaxe ByIdFkUE navigue dans la structure de l'entité :
     * Id (le champ de la clé composée) -> FkUE (le champ dans la classe IdAcquis).
     *
     * @param codeUE Le code de l'UE (partie de la clé composée).
     * @return Le nombre d'acquis associés à ce code.
     */
    int countByIdFkUE(String codeUE);

}