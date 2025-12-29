package org.isfce.pid.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.isfce.pid.dao.*;
import org.isfce.pid.model.*;
import org.isfce.pid.service.KnowledgeBaseService.RuleMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("testU")
@SpringBootTest
@Transactional
class TestKnowledgeBaseService {

    @Autowired
    private KnowledgeBaseService kbService;

    // DAOs
    @Autowired private IExemptionRequestDao reqDao;
    @Autowired private IExternalCourseDao extCourseDao;
    @Autowired private IKbCorrespondenceRuleDao ruleDao;
    @Autowired private IKbSchoolDao schoolDao;
    @Autowired private IKbCourseDao kbCourseDao; 
    @Autowired private IUeDao ueDao;
    @Autowired private IStudentDao studentDao;
    @Autowired private ISectionDao sectionDao;

    private ExemptionRequest request;
    private KbSchool ulb;
    private UE ueIpap;

    @BeforeEach
    void setUp() {
        // 1. Création de l'École
        ulb = KbSchool.builder()
                .code("ULB")
                .etablissement("Université Libre de Bruxelles")
                .urlProgramme("https://ulb.be/prog")
                .build();
        ulb = schoolDao.save(ulb); 

        // 2. Création de l'UE Cible
        ueIpap = UE.builder()
                .code("IPAP")
                .ref("REF_IPAP")
                .nom("Introduction à la Programmation")
                .nbPeriodes(60)
                .ects(6)
                .prgm("Contenu du cours de Java...")
                .build();
        ueIpap = ueDao.save(ueIpap);

        // 3. Création Section et Etudiant
        Section section = new Section("INFO", "Informatique", new HashSet<>());
        section = sectionDao.save(section); 

        Student student = Student.builder()
                .email("test@kb.be")
                .nom("Test")
                .prenom("KB")
                .section(section)
                .build();
        student = studentDao.save(student);

        // 4. Création de la Demande
        request = ExemptionRequest.builder()
                .etudiant(student)
                .section(section)
                .statut(StatutDemande.DRAFT)
                .externalCourses(new HashSet<>())
                .build();
        request = reqDao.save(request);
    }

    @Test
    @DisplayName("Matching Simple : 1 Cours Externe -> 1 Règle")
    void testFindMatchingRules_Simple() {
        // CONFIG : Règle "Java ULB donne IPAP"
        createRule("Règle Java", ulb, "JAVA101", 5, ueIpap);

        // DONNÉE ÉTUDIANT : Il a "JAVA101" à l'ULB avec 6 ECTS
        addExternalCourseToRequest("ULB", "JAVA101", "Java Basics", 6);

        // ACTION
        List<RuleMatch> matches = kbService.findMatchingRules(request.getExternalCourses());

        // VERIF
        assertEquals(1, matches.size());
        assertEquals("Règle Java", matches.get(0).rule().getDescription());
        assertEquals(1, matches.get(0).studentCourses().size());
    }

    @Test
    @DisplayName("Matching Complexe : 2 Cours Externes (Puzzle) -> 1 Règle")
    void testFindMatchingRules_Complex_CombinedCourses() {
        // CONFIG : Règle "Algo 1 + Algo 2 donnent IPAP" (Min 8 ECTS cumulés)
        
        KbCorrespondenceRule rule = KbCorrespondenceRule.builder()
                .description("Algo Combiné")
                .minTotalEcts(8)
                .ecole(ulb)
                .build();
        
        // Ingrédients KB 
        KbCourse src1 = KbCourse.builder().ecole(ulb).code("ALGO1").libelle("Algo 1").ects(4).build();
        src1 = kbCourseDao.save(src1); 

        KbCourse src2 = KbCourse.builder().ecole(ulb).code("ALGO2").libelle("Algo 2").ects(4).build();
        src2 = kbCourseDao.save(src2); 
        
        rule.setSources(Set.of(
            KbCorrespondenceRuleSource.builder().rule(rule).cours(src1).build(),
            KbCorrespondenceRuleSource.builder().rule(rule).cours(src2).build()
        ));
        
        rule.setTargets(Set.of(
            KbCorrespondenceRuleTarget.builder().rule(rule).ue(ueIpap).build()
        ));
        
        ruleDao.save(rule);

        // DONNÉE ÉTUDIANT : Il a les deux cours
        addExternalCourseToRequest("ULB", "ALGO1", "Algo 1", 4);
        addExternalCourseToRequest("ULB", "ALGO2", "Algo 2", 5);

        // ACTION
        List<RuleMatch> matches = kbService.findMatchingRules(request.getExternalCourses());

        // VERIF
        assertEquals(1, matches.size(), "Devrait trouver une règle combinée");
        RuleMatch match = matches.get(0);
        assertEquals(2, match.studentCourses().size(), "Devrait utiliser les 2 cours");
    }

    @Test
    @DisplayName("Echec Matching : ECTS Insuffisants")
    void testFindMatchingRules_Fail_NotEnoughEcts() {
        // CONFIG : Règle demande 10 ECTS
        createRule("Grosse Règle", ulb, "BIG_JAVA", 10, ueIpap);

        // DONNÉE ÉTUDIANT : Il a le bon cours, mais seulement 5 ECTS
        addExternalCourseToRequest("ULB", "BIG_JAVA", "Java Light", 5);

        // ACTION
        List<RuleMatch> matches = kbService.findMatchingRules(request.getExternalCourses());

        // VERIF
        assertTrue(matches.isEmpty(), "Ne devrait pas matcher car 5 ECTS < 10 requis");
    }

    @Test
    @DisplayName("Echec Matching : Mauvaise École")
    void testFindMatchingRules_Fail_WrongSchool() {
        // CONFIG : Règle pour ULB
        createRule("Règle ULB", ulb, "JAVA", 5, ueIpap);

        // DONNÉE ÉTUDIANT : Il a "JAVA" mais à l'UCL (pas ULB)
        addExternalCourseToRequest("UCL", "JAVA", "Java UCL", 6);

        // ACTION
        List<RuleMatch> matches = kbService.findMatchingRules(request.getExternalCourses());

        // VERIF
        assertTrue(matches.isEmpty(), "Ne devrait pas matcher car l'école est différente");
    }

    // ——— HELPERS ———

    private void createRule(String description, KbSchool school, String sourceCode, int minEcts, UE targetUE) {
        KbCorrespondenceRule rule = KbCorrespondenceRule.builder()
                .description(description)
                .minTotalEcts(minEcts)
                .ecole(school)
                .build();

        KbCourse srcCourse = KbCourse.builder()
                .ecole(school)
                .code(sourceCode)
                .libelle(description)
                .ects(minEcts)
                .build();
        
        srcCourse = kbCourseDao.save(srcCourse); 

        rule.setSources(Set.of(KbCorrespondenceRuleSource.builder()
                .rule(rule)
                .cours(srcCourse)
                .build()));

        rule.setTargets(Set.of(KbCorrespondenceRuleTarget.builder()
                .rule(rule)
                .ue(targetUE)
                .build()));

        ruleDao.save(rule);
    }

    private void addExternalCourseToRequest(String schoolName, String code, String libelle, int ects) {
        ExternalCourse c = ExternalCourse.builder()
                .etablissement(schoolName)
                .code(code)
                .libelle(libelle)
                .ects(ects)
                .request(request)
                .build();
        extCourseDao.save(c);
        request.getExternalCourses().add(c);
    }
}