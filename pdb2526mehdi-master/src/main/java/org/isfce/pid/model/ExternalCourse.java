package org.isfce.pid.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "external_course", indexes = @Index(columnList = "etablissement,code"))
public class ExternalCourse extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id") 
    @ToString.Exclude
    private ExemptionRequest request;

    @NotBlank @Column(nullable = false, length = 100)
    private String etablissement; // ULB, HE2B, ...

    @NotBlank @Column(nullable = false, length = 32)
    private String code;          // code du cours externe

    @NotBlank @Column(nullable = false)
    private String libelle;

    @Min(value = 1, message = "{err.externalcourse.ects.min}") 
    @Column(nullable = false)
    private int ects;

    private String urlProgramme;  
    
    @Builder.Default
    @OneToMany(mappedBy = "externalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupportingDocument> documents = new HashSet<>();
}