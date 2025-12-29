package org.isfce.pid.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.isfce.pid.dto.AcquisDto;
import org.isfce.pid.dto.AcquisFullDto;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.dto.UEDto;
import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.UE;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface de mapping (MapStruct) pour la gestion des Unités d'Enseignement (UE) et des Acquis.
 * Ce mapper gère les conversions bidirectionnelles entre les entités JPA ({@link UE}, {@link Acquis})
 * et leurs différents DTOs (Lazy, Full, AcquisDto).
 * L'implémentation est générée automatiquement par Spring lors de la compilation.
 */
@Mapper(componentModel = "spring")
public interface UEMapper {

    // ————— MAPPING UE -> DTO —————

    /**
     * Convertit une entité UE vers son DTO léger (sans la liste des acquis).
     *
     * @param ue L'entité UE source.
     * @return Le DTO léger correspondant.
     */
    UEDto toUELazyDto(UE ue);

    /**
     * Convertit une liste d'entités UE vers une liste de DTOs légers.
     *
     * @param liste La liste des entités UE.
     * @return La liste des DTOs légers.
     */
    List<UEDto> toListUELazyDto(List<UE> liste);

    /**
     * Convertit une entité UE vers son DTO complet (incluant la liste des acquis).
     *
     * @param ue L'entité UE source.
     * @return Le DTO complet.
     */
    @Mapping(source = "acquis", target = "acquis")
    UEFullDto toUEFullDto(UE ue);

    /**
     * Convertit une liste d'entités UE vers une liste de DTOs complets.
     *
     * @param liste La liste des entités UE.
     * @return La liste des DTOs complets.
     */
    List<UEFullDto> toListUEFullDto(List<UE> liste);

    // ————— MAPPING DTO -> UE —————

    /**
     * Convertit un DTO léger en entité UE.
     * La liste des acquis est ignorée (car absente du DTO source).
     *
     * @param ueDto Le DTO léger source.
     * @return L'entité UE.
     */
    @Mapping(target = "acquis", ignore = true)
    UE fromUEDto(UEDto ueDto);

    /**
     * Reconstruit une entité UE complète à partir de son DTO Full.
     * Utilise une méthode Java personnalisée pour mapper correctement la liste des acquis.
     *
     * @param ueDto Le DTO complet source.
     * @return L'entité UE avec ses acquis.
     */
    @Mapping(target = "acquis", expression = "java(mapAcquisList(ueDto))")
    UE fromUEFullDto(UEFullDto ueDto);

    // ————— MÉTHODES PAR DÉFAUT (Helpers pour relations complexes) —————

    /**
     * Helper : Crée une entité Acquis à partir d'un DTO imbriqué (sans FK) et du code UE parent.
     * Reconstruit la clé composée {@link Acquis.IdAcquis}.
     */
    default Acquis toAcquis(AcquisFullDto dto, String codeUE) {
        if (dto == null)
            return null;
        return new Acquis(new Acquis.IdAcquis(codeUE, dto.getNum()), dto.getAcquis(), dto.getPourcentage());
    }

    /**
     * Helper : Mappe la liste des acquis d'un DTO complet vers une liste d'entités.
     * Propage le code de l'UE parente vers chaque enfant Acquis.
     */
    default List<Acquis> mapAcquisList(UEFullDto dto) {
        if (dto.getAcquis() == null)
            return List.of();
        return dto.getAcquis().stream().map(a -> toAcquis(a, dto.getCode()))
                .collect(Collectors.toList());
    }

    // ————— MAPPING ACQUIS (DTO avec Clé Étrangère explicite) —————

    /**
     * Convertit une entité Acquis vers un DTO plat (avec champs FK explicites).
     *
     * @param acquis L'entité source.
     * @return Le DTO contenant les IDs séparés.
     */
    @Mapping(source = "id.fkUE", target = "fkUE")
    @Mapping(source = "id.num", target = "num")
    AcquisDto toAcquisDto(Acquis acquis);

    /**
     * Convertit un DTO d'acquis vers l'entité, en reconstruisant l'ID composé.
     *
     * @param acquisDto Le DTO source.
     * @return L'entité Acquis.
     */
    @Mapping(target = "id", expression = "java(new Acquis.IdAcquis(acquisDto.getFkUE(), acquisDto.getNum()))")
    Acquis toAcquis(AcquisDto acquisDto);

    /**
     * Convertit une liste de DTOs d'acquis vers une liste d'entités modifiable.
     *
     * @param dtos La liste des DTOs.
     * @return La liste des entités.
     */
    default List<Acquis> toListAcquis(List<AcquisDto> dtos) {
        if (dtos == null)
            return null;
        return dtos.stream().map(this::toAcquis).collect(Collectors.toList());
    }

    /**
     * Convertit une liste d'entités Acquis vers une liste de DTOs.
     *
     * @param acquis La liste des entités.
     * @return La liste des DTOs.
     */
    List<AcquisDto> toListAcquisDto(List<Acquis> acquis);

    // ————— MAPPING ACQUIS (DTO Full sans FK) —————

    /**
     * Convertit une entité Acquis vers un DTO imbriqué (sans répétition de la FK UE).
     *
     * @param acquis L'entité source.
     * @return Le DTO allégé pour l'imbrication.
     */
    @Mapping(source = "id.num", target = "num")
    @Mapping(source = "acquis", target = "acquis")
    @Mapping(source = "pourcentage", target = "pourcentage")
    AcquisFullDto toAcquisFullDto(Acquis acquis);

}