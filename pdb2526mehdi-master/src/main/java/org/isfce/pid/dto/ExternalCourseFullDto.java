package org.isfce.pid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * DTO (Data Transfer Object) "Complet" représentant un cours externe.
 * Étend {@link ExternalCourseDto}.
 * Actuellement identique à son parent en termes de structure de données,
 * cette classe sert de marqueur sémantique pour les vues détaillées (Detail View)
 * ou pour de futures extensions spécifiques qui ne doivent pas apparaître dans les listes.
 */
@Data
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ExternalCourseFullDto extends ExternalCourseDto {



}