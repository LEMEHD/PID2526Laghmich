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

    // CORRECTION ICI : "hasDocAttached" (et pas "hasProgrammeAttached")
    @Mapping(target = "hasDocAttached", expression = "java(course.getDocuments() != null && !course.getDocuments().isEmpty())")
    ExternalCourseDto toExternalCourseDto(ExternalCourse course);

    SupportingDocumentDto toSupportingDocumentDto(SupportingDocument doc);

    ExemptionItemDto toExemptionItemDto(ExemptionItem item);

    // ————— MAPPINGS REQUEST (Le Dossier) —————

    // VERSION LÉGÈRE : Pas de mapping de documents ici
    @Mapping(source = "section.nom", target = "sectionNom")
    @Mapping(source = "section.code", target = "sectionCode")
    ExemptionRequestDto toExemptionRequestDto(ExemptionRequest req);

    // VERSION COMPLÈTE : Mapping des documents globaux ici
    @InheritConfiguration(name = "toExemptionRequestDto")
    @Mapping(source = "globalDocuments", target = "documents")
    ExemptionRequestFullDto toExemptionRequestFullDto(ExemptionRequest req);

    // ————— DTO SPÉCIFIQUE (FULL COURSE) —————
    
    // Mapping des documents spécifiques du cours vers la liste 'programmes'
    @Mapping(source = "documents", target = "programmes")
    @InheritConfiguration(name = "toExternalCourseDto") 
    ExternalCourseFullDto toExternalCourseFullDto(ExternalCourse course);

    // ————— CONVERSIONS DE LISTES —————

    List<ExemptionRequestDto> toExemptionRequestDtoList(List<ExemptionRequest> reqs);

    Set<ExternalCourseDto> toExternalCourseDtoSet(Set<ExternalCourse> courses);

    Set<SupportingDocumentDto> toSupportingDocumentDtoSet(Set<SupportingDocument> docs);
    
    Set<ExemptionItemDto> toExemptionItemDtoSet(Set<ExemptionItem> items);
}