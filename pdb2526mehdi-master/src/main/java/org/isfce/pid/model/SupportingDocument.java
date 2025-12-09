package org.isfce.pid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "supporting_document")
public class SupportingDocument extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id") 
    @ToString.Exclude
    private ExemptionRequest request;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20)
    private TypeDocument type;

    @NotBlank 
    @Column(nullable = false)
    private String urlStockage; // Chemin physique ou URL S3


}