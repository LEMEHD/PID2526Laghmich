package org.isfce.pid.mapper;

import java.util.List;
import java.util.Set;

import org.isfce.pid.dto.*;
import org.isfce.pid.model.*;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Interface de mapping (MapStruct) pour la gestion du dossier de dispense.
 * Centralise les conversions entre les entités du domaine (ExemptionRequest, ExternalCourse, etc.)
 * et les objets de transfert de données (DTO) utilisés par l'API REST.
 * Utilise {@link UEMapper} pour les sous-mappings liés aux UEs.
 */
@Mapper(componentModel = "spring", uses = {UEMapper.class})
public interface ExemptionMapper {

    // ————— MAPPINGS UNITAIRES (Entité -> DTO) —————

    /**
     * Convertit un étudiant en DTO.
     * Mappe le code de la section associée.
     *
     * @param student L'entité étudiant.
     * @return Le DTO étudiant.
     */
    @Mapping(source = "section.code", target = "sectionCode")
    StudentDto toStudentDto(Student student);

    /**
     * Convertit un cours externe en DTO léger (pour les listes).
     * Calcule dynamiquement si des documents sont attachés (`hasDocAttached`)
     * et mappe la liste des documents vers le champ `programmes`.
     *
     * @param course Le cours externe.
     * @return Le DTO léger.
     */
    @Named("toCourseLight")
    @Mapping(target = "hasDocAttached", source = "course", qualifiedByName = "checkDocs")
    @Mapping(source = "documents", target = "programmes")
    ExternalCourseDto toExternalCourseDto(ExternalCourse course);

    /**
     * Méthode utilitaire (Helper) pour vérifier la présence de documents.
     *
     * @param course Le cours à vérifier.
     * @return true si la liste de documents n'est ni null ni vide.
     */
    @Named("checkDocs")
    default boolean checkDocs(ExternalCourse course) {
        if (course == null || course.getDocuments() == null) {
            return false;
        }
        return !course.getDocuments().isEmpty();
    }

    /**
     * Convertit un document justificatif en DTO.
     *
     * @param doc L'entité document.
     * @return Le DTO document.
     */
    SupportingDocumentDto toSupportingDocumentDto(SupportingDocument doc);

    /**
     * Convertit une ligne de dispense (Item) en DTO.
     *
     * @param item L'entité item.
     * @return Le DTO item.
     */
    ExemptionItemDto toExemptionItemDto(ExemptionItem item);

    // ————— MAPPINGS REQUEST (Le Dossier) —————

    /**
     * Convertit une demande de dispense en DTO léger.
     * Destiné aux listes et tableaux de bord (charge réduite).
     * Inclut le nom et le code de la section.
     *
     * @param req La demande de dispense.
     * @return Le DTO léger de la demande.
     */
    @Named("toRequestLight")
    @Mapping(source = "section.nom", target = "sectionNom")
    @Mapping(source = "section.code", target = "sectionCode")
    ExemptionRequestDto toExemptionRequestDto(ExemptionRequest req);

    /**
     * Convertit une demande de dispense en DTO complet.
     * Destiné à l'affichage détaillé du dossier.
     * Inclut les documents globaux.
     *
     * @param req La demande de dispense.
     * @return Le DTO complet de la demande.
     */
    @InheritConfiguration(name = "toExemptionRequestDto")
    @Mapping(source = "globalDocuments", target = "documents")
    ExemptionRequestFullDto toExemptionRequestFullDto(ExemptionRequest req);

    // ————— DTO SPÉCIFIQUE (FULL COURSE) —————

    /**
     * Convertit un cours externe en DTO complet.
     * Hérite de la configuration du DTO léger mais est typé pour fournir
     * tous les détails nécessaires à l'édition.
     *
     * @param course Le cours externe.
     * @return Le DTO complet du cours externe.
     */
    @Mapping(source = "documents", target = "programmes")
    @InheritConfiguration(name = "toExternalCourseDto")
    ExternalCourseFullDto toExternalCourseFullDto(ExternalCourse course);

    // ————— CONVERSIONS DE COLLECTIONS —————

    /**
     * Convertit une liste de demandes vers une liste de DTOs légers.
     */
    @IterableMapping(qualifiedByName = "toRequestLight")
    List<ExemptionRequestDto> toExemptionRequestDtoList(List<ExemptionRequest> reqs);

    /**
     * Convertit un set de cours externes vers un set de DTOs légers.
     */
    @IterableMapping(qualifiedByName = "toCourseLight")
    Set<ExternalCourseDto> toExternalCourseDtoSet(Set<ExternalCourse> courses);

    /**
     * Convertit un set de documents vers un set de DTOs.
     */
    Set<SupportingDocumentDto> toSupportingDocumentDtoSet(Set<SupportingDocument> docs);

    /**
     * Convertit un set d'items de dispense vers un set de DTOs.
     */
    Set<ExemptionItemDto> toExemptionItemDtoSet(Set<ExemptionItem> items);
}