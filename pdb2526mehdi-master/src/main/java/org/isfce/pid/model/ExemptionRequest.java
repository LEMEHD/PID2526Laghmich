package org.isfce.pid.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.HashSet;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "exemption_request")
public class ExemptionRequest extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id") // Toujours mieux de nommer
    @ToString.Exclude
    private Student etudiant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "section_code") 
    private Section section;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutDemande statut = StatutDemande.DRAFT;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default @ToString.Exclude
    private Set<ExternalCourse> externalCourses = new HashSet<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default @ToString.Exclude
    private Set<SupportingDocument> documents = new HashSet<>();

    // Les demandes concrètes de dispenses (Lignes du bulletin final)
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default @ToString.Exclude
    private Set<ExemptionItem> items = new HashSet<>();

    // Helpers
    public void addExternalCourse(ExternalCourse c){ c.setRequest(this); externalCourses.add(c); }
    public void addDocument(SupportingDocument d){ d.setRequest(this); documents.add(d); }
    public void addItem(ExemptionItem i){ i.setRequest(this); items.add(i); }
}