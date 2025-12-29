package org.isfce.pid.dao;

import java.util.UUID;

import org.isfce.pid.model.SupportingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface d'accès aux données (DAO/Repository) pour l'entité SupportingDocument.
 * Gère les métadonnées des fichiers justificatifs (nom, type, chemin de stockage).
 * Note : Ce DAO gère les enregistrements en base de données, pas le stockage physique
 * des fichiers (qui est généralement géré par un Service dédié).
 */
@Repository
public interface ISupportingDocumentDao extends JpaRepository<SupportingDocument, UUID> {


}