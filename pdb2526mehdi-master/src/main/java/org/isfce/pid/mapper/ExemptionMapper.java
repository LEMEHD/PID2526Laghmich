package org.isfce.pid.mapper;

import java.util.List;
import java.util.Set;

import org.isfce.pid.dto.*;
import org.isfce.pid.model.*;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UEMapper.class})
public interface ExemptionMapper {

    // ————— MAPPINGS UNITAIRES —————

    @Mapping(source = "section.code", target = "sectionCode")
    StudentDto toStudentDto(Student student);

    // AJOUT : Calcul du flag pour savoir s'il y a des docs sans les charger
    @Mapping(target = "hasProgrammeAttached", expression = "java(course.getDocuments() != null && !course.getDocuments().isEmpty())")
    ExternalCourseDto toExternalCourseDto(ExternalCourse course);

    SupportingDocumentDto toSupportingDocumentDto(SupportingDocument doc);

    ExemptionItemDto toExemptionItemDto(ExemptionItem item);

    // ————— MAPPINGS REQUEST (Le Dossier) —————

    @Mapping(source = "section.nom", target = "sectionNom")
    @Mapping(source = "section.code", target = "sectionCode")
    @Mapping(source = "globalDocuments", target = "documents") 
    ExemptionRequestDto toExemptionRequestDto(ExemptionRequest req);

    @InheritConfiguration(name = "toExemptionRequestDto")
    ExemptionRequestFullDto toExemptionRequestFullDto(ExemptionRequest req);

    // ————— DTO SPÉCIFIQUE (FULL COURSE) —————
    
    // C'est parfait : on mappe les documents de l'entité vers 'programmes' du DTO
    @Mapping(source = "documents", target = "programmes")
    // On hérite aussi du mapping 'hasProgrammeAttached' défini plus haut
    @InheritConfiguration(name = "toExternalCourseDto") 
    ExternalCourseFullDto toExternalCourseFullDto(ExternalCourse course);

    // ————— CONVERSIONS DE LISTES —————

    List<ExemptionRequestDto> toExemptionRequestDtoList(List<ExemptionRequest> reqs);

    Set<ExternalCourseDto> toExternalCourseDtoSet(Set<ExternalCourse> courses);

    Set<SupportingDocumentDto> toSupportingDocumentDtoSet(Set<SupportingDocument> docs);
    
    // Si ton ExternalCourseFullDto utilise une List, MapStruct le gérera 
    // automatiquement à partir du Set de l'entité.
    
    Set<ExemptionItemDto> toExemptionItemDtoSet(Set<ExemptionItem> items);
}