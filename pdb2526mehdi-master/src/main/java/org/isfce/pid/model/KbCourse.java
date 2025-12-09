package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(
    name = "kb_course",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ecole_id", "code"})
)

public class KbCourse extends BaseEntity {

    // École d'origine (ULB, HELB, ...).

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private KbSchool ecole;

    // Code officiel du cours dans l'école externe :
    // ex. "INFO-F101", "BINV2090-2", ...
    @Column(nullable = false, length = 64)
    private String code;
     
    @Column(nullable = false)
    private String libelle;
   
    @Column(nullable = false)
    private int ects;

    private String urlProgramme;
}
