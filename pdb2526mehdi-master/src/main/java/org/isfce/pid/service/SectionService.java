package org.isfce.pid.service;

import java.util.List;

import org.isfce.pid.dao.ISectionDao;
import org.isfce.pid.dto.SectionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service métier gérant les Sections d'enseignement.
 * Fournit principalement les données référentielles nécessaires aux listes
 * déroulantes dans l'interface utilisateur.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {

    private final ISectionDao sectionDao;

    /**
     * Récupère la liste complète des sections disponibles.
     * Utilise une projection DTO pour optimiser les performances en ne chargeant
     * que les données essentielles (Code et Nom).
     * @return La liste des sections sous forme de DTO légers.
     */
    public List<SectionDto> getListeSections() {
        return sectionDao.findAllAsDtos();
    }
}