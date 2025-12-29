package org.isfce.pid.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.isfce.pid.controller.error.DuplicateException;
import org.isfce.pid.dao.IUeDao;
import org.isfce.pid.dto.UEDto;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.mapper.UEMapper;
import org.isfce.pid.model.UE;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service gérant la logique métier des Unités d'Enseignement (UE).
 * Sépare clairement la création (avec acquis) de la mise à jour (sans toucher aux acquis)
 * pour éviter les conflits de mapping et les erreurs Hibernate sur les Records.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UEService {

    private final IUeDao ueDao;
    private final UEMapper mapper;
    private final MessageSource messageSource;

    /**
     * Récupère la liste simplifiée de toutes les UEs.
     *
     * @return La liste des UEs (Code + Nom + ECTS...).
     */
    @Transactional(readOnly = true)
    public List<UEDto> getListeUE() {
        return ueDao.findAllAsDtos();
    }

    /**
     * Récupère le détail complet d'une UE par son code.
     *
     * @param code Le code unique de l'UE (ex: "IPAP").
     * @return Le DTO complet incluant les acquis.
     * @throws NoSuchElementException Si le code ne correspond à aucune UE.
     */
    public UEFullDto getUE(String code) {
        return ueDao.findById(code)
                .map(mapper::toUEFullDto)
                .orElseThrow(() -> new NoSuchElementException(msg("err.notFound", code)));
    }

    /**
     * Crée une NOUVELLE UE avec ses acquis initiaux.
     * Lève une exception si l'UE existe déjà (pas d'écrasement).
     *
     * @param ueDto Le DTO complet contenant les informations à créer.
     * @return Le DTO de l'UE sauvegardée.
     * @throws DuplicateException Si une UE avec le même code existe déjà.
     */
    public UEFullDto addUE(UEFullDto ueDto) {
        if (ueDao.existsById(ueDto.getCode())) {
            throw new DuplicateException(msg("err.doublon", "UE"), ueDto.getCode());
        }

        // Conversion DTO -> Entité (inclut la liste des acquis)
        var entity = mapper.fromUEFullDto(ueDto);
        return mapper.toUEFullDto(ueDao.save(entity));
    }

    /**
     * Met à jour une UE existante.
     * Modifie UNIQUEMENT les informations administratives (Nom, Ref, ECTS, Programme).
     * NE TOUCHE PAS à la liste des acquis existants en base de données.
     *
     * @param ueDto Le DTO léger contenant les nouvelles infos.
     * @return Le DTO mis à jour.
     * @throws NoSuchElementException Si l'UE n'existe pas.
     */
    public UEDto updateUE(UEDto ueDto) {
        // 1. Récupération de l'entité gérée (managed) qui contient les vrais acquis
        UE ueExistante = ueDao.findById(ueDto.getCode())
                .orElseThrow(() -> new NoSuchElementException(msg("err.notFound", ueDto.getCode())));

        // 2. Conversion du DTO entrant en objet temporaire
        // (Note: via le mapper, modifications.acquis est null/vide grâce à ignore=true)
        UE modifications = mapper.fromUEDto(ueDto);

        // 3. Transfert manuel des champs simples vers l'entité gérée
        ueExistante.setRef(modifications.getRef());
        ueExistante.setNom(modifications.getNom());
        ueExistante.setEcts(modifications.getEcts());
        ueExistante.setNbPeriodes(modifications.getNbPeriodes());
        ueExistante.setPrgm(modifications.getPrgm());

        // 4. Sauvegarde
        // Hibernate détecte uniquement les changements sur TUE.
        // La table TACQUIS n'est pas touchée, évitant le bug de réflexion sur les Records.
        return mapper.toUELazyDto(ueDao.save(ueExistante));
    }

    /**
     * Supprime une UE de la base de données.
     *
     * @param code Le code de l'UE à supprimer.
     * @throws NoSuchElementException Si l'UE n'existe pas.
     */
    public void deleteUE(String code) {
        if (!ueDao.existsById(code)) {
            throw new NoSuchElementException(msg("err.notFound", code));
        }
        ueDao.deleteById(code);
    }

    /**
     * Vérifie l'existence d'une UE.
     *
     * @param code Le code à vérifier.
     * @return true si trouvée, false sinon.
     */
    @Transactional(readOnly = true)
    public boolean existUE(String code) {
        return ueDao.existsById(code);
    }

    /**
     * Utilitaire interne pour récupérer les messages traduits.
     */
    private String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}