package org.isfce.pid.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "exemption_item")
public class ExemptionItem extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id") // Bonne pratique : nommer la FK
    @ToString.Exclude
    private ExemptionRequest request;

    /**
     * On pointe vers l'entité 'UE' existante (Clé primaire = String code).
     * EAGER est acceptable ici car on affiche rarement une ligne de dispense sans le nom du cours ISFCE.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ue_code") // Explicite que c'est le code de l'UE
    private UE ue; 

    @Builder.Default 
    @Column(nullable = false)
    private boolean totalEctsMatches = false;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20) 
    @Builder.Default
    private DecisionItem decision = DecisionItem.PENDING;

    @Min(value = 0, message = "{err.exemption.note.min}")
    @Max(value = 20, message = "{err.exemption.note.max}")
    @Column(nullable = true) // Nullable car au début, la note n'est pas encore attribuée
    private Integer noteAccordee;
    
    @ManyToMany
    @JoinTable(
        name = "item_course_link",
        joinColumns = @JoinColumn(name = "item_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    private Set<ExternalCourse> justifyingCourses = new HashSet<>();
    
}