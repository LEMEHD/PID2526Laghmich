package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Classe de base abstraite pour toutes les entités persistantes de l'application.
 * Elle standardise la gestion de l'identité technique (UUID) et des métadonnées d'audit
 * (date de création et date de dernière modification) grâce au mécanisme d'audit de Spring Data JPA.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Identifiant unique de l'entité (généré automatiquement sous format UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Horodatage de la création de l'enregistrement.
     * Ce champ est immuable une fois défini.
     */
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    /**
     * Horodatage de la dernière modification de l'enregistrement.
     * Mis à jour automatiquement à chaque sauvegarde.
     */
    @LastModifiedDate
    private Instant updatedAt;
}