package org.isfce.pid.mapper;

import java.util.List;
import java.util.Set;

import org.isfce.pid.dto.ExemptionItemDto;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestFullDto;
import org.isfce.pid.dto.ExternalCourseDto;
import org.isfce.pid.dto.StudentDto;
import org.isfce.pid.dto.SupportingDocumentDto;
import org.isfce.pid.model.ExemptionItem;
import org.isfce.pid.model.ExemptionRequest;
import org.isfce.pid.model.ExternalCourse;
import org.isfce.pid.model.Student;
import org.isfce.pid.model.SupportingDocument;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// 'uses' permet de réutiliser UEMapper pour convertir l'UE dans l'ExemptionItem
@Mapper(componentModel = "spring", uses = {UEMapper.class})
public interface ExemptionMapper {

    // ————— MAPPINGS UNITAIRES —————

    // Student : On extrait le code de la section (Entity -> String)
    @Mapping(source = "section.code", target = "sectionCode")
    StudentDto toStudentDto(Student student);

    // ExternalCourse : Mapping direct (les champs ont les mêmes noms)
    ExternalCourseDto toExternalCourseDto(ExternalCourse course);

    // SupportingDocument : Mapping direct
    SupportingDocumentDto toSupportingDocumentDto(SupportingDocument doc);

    // ExemptionItem : 
    // 1. La conversion UE -> UEDto est gérée automatiquement par UEMapper (grâce au 'uses')
    // 2. La liste justifyingCourses est convertie grâce à la méthode toExternalCourseDtoSet ci-dessous
    ExemptionItemDto toExemptionItemDto(ExemptionItem item);

    // ————— MAPPINGS REQUEST (Le Dossier) —————

    // Version Légère (Pour les listes)
    @Mapping(source = "section.nom", target = "sectionNom")
    @Mapping(source = "section.code", target = "sectionCode")
    ExemptionRequestDto toExemptionRequestDto(ExemptionRequest req);

    // Version Complète (Pour le détail)
    // MapStruct mappe automatiquement les champs parents (via l'héritage du DTO)
    // et s'occupe des collections (externalCourses, documents, items) grâce aux méthodes ci-dessous
    @InheritConfiguration(name = "toExemptionRequestDto") // Reprend les mappings du dessus
    ExemptionRequestFullDto toExemptionRequestFullDto(ExemptionRequest req);

    // ————— CONVERSIONS DE LISTES —————
    // Nécessaires pour que MapStruct sache comment transformer les Set<Entity> en Set<Dto>

    List<ExemptionRequestDto> toExemptionRequestDtoList(List<ExemptionRequest> reqs);

    Set<ExternalCourseDto> toExternalCourseDtoSet(Set<ExternalCourse> courses);

    Set<SupportingDocumentDto> toSupportingDocumentDtoSet(Set<SupportingDocument> docs);

    Set<ExemptionItemDto> toExemptionItemDtoSet(Set<ExemptionItem> items);
}