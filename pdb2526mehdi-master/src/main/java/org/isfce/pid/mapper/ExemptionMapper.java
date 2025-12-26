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

@Mapper(componentModel = "spring", uses = {UEMapper.class})
public interface ExemptionMapper {

    // ————— MAPPINGS UNITAIRES —————

    @Mapping(source = "section.code", target = "sectionCode")
    StudentDto toStudentDto(Student student);

    // 1. DTO LEGER (Pour la liste)
    @Named("toCourseLight") 
    @Mapping(target = "hasDocAttached", source = "course", qualifiedByName = "checkDocs")
    @Mapping(source = "documents", target = "programmes") // <--- AJOUT CRUCIAL (Pour voir les trombones)
    ExternalCourseDto toExternalCourseDto(ExternalCourse course);

    @Named("checkDocs")
    default boolean checkDocs(ExternalCourse course) {
        if (course == null || course.getDocuments() == null) {
            return false;
        }
        return !course.getDocuments().isEmpty();
    }

    // 2. DOCUMENT (On a retiré le "ignore=true" pour le nom du fichier)
    SupportingDocumentDto toSupportingDocumentDto(SupportingDocument doc);

    ExemptionItemDto toExemptionItemDto(ExemptionItem item);

    // ————— MAPPINGS REQUEST (Le Dossier) —————

    @Named("toRequestLight")
    @Mapping(source = "section.nom", target = "sectionNom")
    @Mapping(source = "section.code", target = "sectionCode")
    ExemptionRequestDto toExemptionRequestDto(ExemptionRequest req);

    // Version Complète
    @InheritConfiguration(name = "toExemptionRequestDto")
    @Mapping(source = "globalDocuments", target = "documents")
    ExemptionRequestFullDto toExemptionRequestFullDto(ExemptionRequest req);

    // ————— DTO SPÉCIFIQUE (FULL COURSE) —————
    
    @Mapping(source = "documents", target = "programmes")
    @InheritConfiguration(name = "toExternalCourseDto") 
    ExternalCourseFullDto toExternalCourseFullDto(ExternalCourse course);

    // ————— CONVERSIONS DE LISTES —————

    @IterableMapping(qualifiedByName = "toRequestLight")
    List<ExemptionRequestDto> toExemptionRequestDtoList(List<ExemptionRequest> reqs);

    @IterableMapping(qualifiedByName = "toCourseLight")
    Set<ExternalCourseDto> toExternalCourseDtoSet(Set<ExternalCourse> courses);

    Set<SupportingDocumentDto> toSupportingDocumentDtoSet(Set<SupportingDocument> docs);
    
    Set<ExemptionItemDto> toExemptionItemDtoSet(Set<ExemptionItem> items);
}