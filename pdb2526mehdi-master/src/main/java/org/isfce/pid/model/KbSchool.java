package org.isfce.pid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "kb_school", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class KbSchool extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 32)
    private String code;          // "VINCI", "ULB", "HELB", ...

    @NotBlank
    @Column(nullable = false)
    private String etablissement; // libellé humain

    private String urlProgramme;  // URL principale vers le programme
}
