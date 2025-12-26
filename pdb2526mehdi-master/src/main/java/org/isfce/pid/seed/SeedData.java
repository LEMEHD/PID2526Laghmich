package org.isfce.pid.seed;

import org.isfce.pid.dao.*; // Tes repositories
import org.isfce.pid.model.*; // Tes models
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // Important !
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Profile("!test") // Évite de lancer ça pendant les tests unitaires si tu veux
public class SeedData {

    // On utilise les repositories qu'on a créés/va créer
    private final IKbSchoolDao schoolDao;
    private final IKbCourseDao courseDao;
    private final IKbCorrespondenceRuleDao ruleDao;
    private final IUeDao ueDao; // Pour récupérer les UE existantes (chargées par data-h2.sql)

    public SeedData(IKbSchoolDao schoolDao, IKbCourseDao courseDao, 
    		IKbCorrespondenceRuleDao ruleDao, IUeDao ueDao) {
        this.schoolDao = schoolDao;
        this.courseDao = courseDao;
        this.ruleDao = ruleDao;
        this.ueDao = ueDao;
    }

    @Bean
    ApplicationRunner initKnowledgeBase() {
        return args -> {
            // On vérifie si la base est vide pour éviter de recréer à chaque restart 
            // (sauf si tu es en create-drop)
            if (schoolDao.count() == 0) {
                System.out.println("🌱 SEEDING : Initialisation de la Base de Connaissances...");
                seedData();
                System.out.println("✅ SEEDING : Terminé.");
            }
        };
    }

    @Transactional
    public void seedData() {
        // 1. Écoles
        KbSchool vinci = createSchool("VINCI", "École Léonard de Vinci", "https://www.vinci.be/en");
        KbSchool ulb   = createSchool("ULB", "Université Libre de Bruxelles", "https://www.ulb.be");
        KbSchool he2b  = createSchool("HE2B", "HE2B ESI", "https://www.he2b.be");
        KbSchool ephec = createSchool("EPHEC", "EPHEC", "https://www.ephec.be");
        KbSchool helb  = createSchool("HELB", "HELB Prigogine", "https://helb.be");

        // 2. Règles Simples (1 pour 1) - Tableaux du PDF
        // VINCI
        createSimpleRule(vinci, "BINV2090-2", "Projet d'intégration", 8, "IPID");
        createSimpleRule(vinci, "BINV1020-1", "POO", 6, "POO");
        createSimpleRule(vinci, "BINV1010-1", "Algo et prog", 6, "IPAP");
        createSimpleRule(vinci, "BINV1030-1", "Bases de données", 5, "IIBD");
        createSimpleRule(vinci, "BINV1060-1", "Systèmes d'OS", 5, "ISE2");
        createSimpleRule(vinci, "BINV2040-1", "Analyse", 5, "IPAI");
        createSimpleRule(vinci, "BINV1080-2", "Organisation", 5, "XORG");
        createSimpleRule(vinci, "BINV1073-1", "Structure ordis", 3, "ISO2");
        createSimpleRule(vinci, "BINV-1090-1", "Maths", 4, "IMA2");
        createSimpleRule(vinci, "BINV1052-1", "Web", 1, "IWPB");

        // ULB
        createSimpleRule(ulb, "INFO-F101", "Programmation", 10, "IPAP");
        createSimpleRule(ulb, "INFO-F102", "Fonct. des ordinateurs", 5, "ISO2");
        createSimpleRule(ulb, "INFO-H303", "Bases de données", 5, "IPAI");

        // HE2B
        createSimpleRule(he2b, "2DON1A", "Base de données", 4, "IIBD");
        createSimpleRule(he2b, "2DEV2A", "Dév 2", 6, "IPO2");
        createSimpleRule(he2b, "1ALG1A", "Algo 1", 4, "IPAP"); // Note: Seulement 4 ECTS -> NEEDS_REVIEW
        createSimpleRule(he2b, "2WEB1A", "Web 1", 6, "IWPB");
        createSimpleRule(he2b, "1MAT1A", "Maths", 4, "IMA2");
        createSimpleRule(he2b, "1EXP1A", "Systèmes", 5, "ISE2");

        // EPHEC
        createSimpleRule(ephec, "RESEAUX1", "Réseaux 1", 5, "IBR2");
        createSimpleRule(ephec, "SYS-EXP1", "Systèmes OS 1", 5, "ISE2");

        // HELB
        createSimpleRule(helb, "IODA0101-2", "Algo & Prog", 5, "IPAP");
        createSimpleRule(helb, "IODA0107-1", "Projet", 3, "IPO2");

        // 3. Règles Complexes (Pour tes tests de logique avancée)
        seedComplexCases(he2b, ephec);
    }

    private void seedComplexCases(KbSchool he2b, KbSchool ephec) {
        // --- Cas N vers 1 (Le Puzzle) : HE2B Algo 1 + Algo 2 -> IPAP ---
        // On réutilise le cours "1ALG1A" déjà créé plus haut, on crée juste le 2ème
        KbCourse algo1 = courseDao.findByEcoleAndCodeIgnoreCase(he2b, "1ALG1A").orElseThrow();
        KbCourse algo2 = createCourse(he2b, "2ALG2A", "Algo 2", 4);

        createComplexRule(he2b, 
            "HE2B Algo 1 + Algo 2 -> IPAP", 
            8, // ECTS Requis
            List.of(algo1, algo2), // Sources
            List.of("IPAP")        // Cibles
        );

        // --- Cas 1 vers N (Le Couteau Suisse) : EPHEC Infra -> ISE2 + IBR2 ---
        KbCourse infra = createCourse(ephec, "INFRA-GLOBAL", "Infrastructure IT Globale", 10);

        createComplexRule(ephec,
            "EPHEC Infra -> Systèmes + Réseaux",
            10, // ECTS Requis
            List.of(infra),         // Sources
            List.of("ISE2", "IBR2") // Cibles
        );
    }

    // --- Helpers (Version simplifiée) ---


    private KbSchool createSchool(String code, String nom, String url) {
        return schoolDao.save(KbSchool.builder().code(code).etablissement(nom).urlProgramme(url).build());
    }
    
    private KbCourse createCourse(KbSchool school, String code, String libelle, int ects) {
        // Vérifie si existe déjà pour éviter doublons si appel multiple
        return courseDao.findByEcoleAndCodeIgnoreCase(school, code)
                .orElseGet(() -> courseDao.save(KbCourse.builder()
                        .ecole(school).code(code).libelle(libelle).ects(ects)
                        .urlProgramme("https://prog." + school.getCode() + ".be/" + code)
                        .build()));
    }

    // Helper pour le cas standard 1-1
    private void createSimpleRule(KbSchool school, String extCode, String extLibelle, int extEcts, String targetUeCode) {
        KbCourse course = createCourse(school, extCode, extLibelle, extEcts);
        createComplexRule(school, school.getCode() + " " + extCode + " -> " + targetUeCode, extEcts, List.of(course), List.of(targetUeCode));
    }

    // Helper générique pour TOUS les cas (1-1, N-1, 1-N)
    private void createComplexRule(KbSchool school, String description, int minEcts, List<KbCourse> sources, List<String> targetUeCodes) {
        KbCorrespondenceRule rule = KbCorrespondenceRule.builder()
                .ecole(school)
                .description(description)
                .minTotalEcts(minEcts)
                .build();

        // Ajout des sources
        sources.forEach(src -> rule.addSource(KbCorrespondenceRuleSource.builder().rule(rule).cours(src).build()));
        
        // Ajout des cibles
        targetUeCodes.forEach(ueCode -> {
            ueDao.findById(ueCode).ifPresent(ue -> 
                rule.addTarget(KbCorrespondenceRuleTarget.builder().rule(rule).ue(ue).build())
            );
        });

        ruleDao.save(rule);
    }
}