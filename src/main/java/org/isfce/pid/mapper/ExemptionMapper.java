package org.isfce.pid.mapper;

import java.util.List;
import java.util.Set;

import org.isfce.pid.dto.*;
import org.isfce.pid.model.*;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.IterableMapping; // <--- AJOUT IMPORTANT
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named; 

@Mapper(componentModel = "spring", uses = {UEMapper.class})
public interface ExemptionMapper {

    // ————— MAPPINGS UNITAIRES —————

    @Mapping(source = "section.code", target = "sectionCode")
    StudentDto toStudentDto(Student student);

    // 1. On donne un nom "Light" à cette méthode pour lever l'ambiguïté plus bas
    @Named("toCourseLight") 
    @Mapping(target = "hasDocAttached", source = "course", qualifiedByName = "checkDocs")
    ExternalCourseDto toExternalCourseDto(ExternalCourse course);

    @Named("checkDocs")
    default boolean checkDocs(ExternalCourse course) {
        if (course == null || course.getDocuments() == null) {
            return false;
        }
        return !course.getDocuments().isEmpty();
    }

    // Correction du warning "originalFileName"
    @Mapping(target = "originalFileName", ignore = true) 
    SupportingDocumentDto toSupportingDocumentDto(SupportingDocument doc);

    ExemptionItemDto toExemptionItemDto(ExemptionItem item);

    // ————— MAPPINGS REQUEST (Le Dossier) —————

    // 2. On donne un nom "Light" ici aussi
    @Named("toRequestLight")
    @Mapping(source = "section.nom", target = "sectionNom")
    @Mapping(source = "section.code", target = "sectionCode")
    ExemptionRequestDto toExemptionRequestDto(ExemptionRequest req);

    // Version Complète (Hérite de la config mais pas du nom "Light")
    @InheritConfiguration(name = "toExemptionRequestDto")
    @Mapping(source = "globalDocuments", target = "documents")
    ExemptionRequestFullDto toExemptionRequestFullDto(ExemptionRequest req);

    // ————— DTO SPÉCIFIQUE (FULL COURSE) —————
    
    @Mapping(source = "documents", target = "programmes")
    @InheritConfiguration(name = "toExternalCourseDto") 
    ExternalCourseFullDto toExternalCourseFullDto(ExternalCourse course);

    // ————— CONVERSIONS DE LISTES —————

    // 3. On dit explicitement : "Pour la liste, utilise la version Light !"
    @IterableMapping(qualifiedByName = "toRequestLight")
    List<ExemptionRequestDto> toExemptionRequestDtoList(List<ExemptionRequest> reqs);

    // 4. Idem ici
    @IterableMapping(qualifiedByName = "toCourseLight")
    Set<ExternalCourseDto> toExternalCourseDtoSet(Set<ExternalCourse> courses);

    Set<SupportingDocumentDto> toSupportingDocumentDtoSet(Set<SupportingDocument> docs);
    
    Set<ExemptionItemDto> toExemptionItemDtoSet(Set<ExemptionItem> items);
}