package org.isfce.pid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "supporting_document")
public class SupportingDocument extends BaseEntity {

	// Lien vers la request (ex: bulletin global)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id") 
    @ToString.Exclude
    private ExemptionRequest request;
    
    // Lien vers le cours externe (ex: attestation de réussite)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_course_id")
    private ExternalCourse externalCourse;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20)
    private TypeDocument type;

    @NotBlank 
    @Column(nullable = false)
    private String urlStockage; // Chemin physique ou URL S3
    
    @Column(name = "original_file_name")
    private String originalFileName;


}