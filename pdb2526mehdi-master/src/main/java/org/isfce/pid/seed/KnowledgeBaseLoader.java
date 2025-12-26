package org.isfce.pid.seed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.isfce.pid.dao.*;
import org.isfce.pid.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Chargeur automatique de la Base de Connaissances au démarrage.
 * Lit le fichier resources/knowledge-base.json et peuple la DB.
 * Remplace l'ancien SeedData "codé en dur".
 */
@Slf4j
@Component
@Profile("!testU") // On évite de lancer ça pendant les tests unitaires
@RequiredArgsConstructor
public class KnowledgeBaseLoader implements CommandLineRunner {

    private final ObjectMapper objectMapper; // Le magicien qui lit le JSON
    private final IKbSchoolDao schoolDao;
    private final IKbCourseDao courseDao;
    private final IKbCorrespondenceRuleDao ruleDao;
    private final IUeDao ueDao;

    // --- DTOs internes (Records) pour mapper la structure du JSON ---
    // Ces records correspondent exactement aux champs de ton fichier JSON
    record JsonCourse(String code, String libelle, int ects) {}
    record JsonRule(String description, int minEcts, List<String> sources, List<String> cibles) {}
    record JsonSchool(String code, String nom, String site, List<JsonCourse> cours, List<JsonRule> regles) {}

    @Override
    @Transactional
    public void run(String... args) {
        // Sécurité : On ne charge rien si la base contient déjà des écoles
        if (schoolDao.count() > 0) {
            log.info("ℹ️ Base de connaissances déjà remplie. Le loader JSON est ignoré.");
            return;
        }

        try {
            log.info("🚀 Chargement de la Base de Connaissances depuis knowledge-base.json...");

            // 1. Lecture du fichier
            InputStream inputStream = new ClassPathResource("knowledge-base.json").getInputStream();
            List<JsonSchool> schoolsData = objectMapper.readValue(inputStream, new TypeReference<>() {});

            // 2. Traitement de chaque école du JSON
            for (JsonSchool schoolData : schoolsData) {
                processSchool(schoolData);
            }

            log.info("✅ Base de Connaissances initialisée avec succès !");

        } catch (Exception e) {
            log.error("❌ Erreur critique lors du chargement de la Knowledge Base", e);
            // On ne bloque pas le démarrage, mais on log l'erreur grave
        }
    }

    private void processSchool(JsonSchool data) {
        // A. Créer ou Récupérer l'école
        KbSchool school = schoolDao.findByCodeIgnoreCase(data.code())
                .orElseGet(() -> schoolDao.save(KbSchool.builder()
                        .code(data.code())
                        .etablissement(data.nom())
                        .urlProgramme(data.site())
                        .build()));

        log.info("   -> École chargée : {}", school.getEtablissement());

        // B. Créer les cours et les garder en mémoire vive (Map) pour lier les règles juste après
        // Clé = Code du cours (ex: "1ALG1A"), Valeur = L'objet KbCourse sauvegardé en DB
        Map<String, KbCourse> courseMap = data.cours().stream()
                .map(c -> createCourse(school, c))
                .collect(Collectors.toMap(KbCourse::getCode, Function.identity()));

        // C. Créer les règles si présentes
        if (data.regles() != null) {
            for (JsonRule ruleData : data.regles()) {
                createRule(school, ruleData, courseMap);
            }
        }
    }

    private KbCourse createCourse(KbSchool school, JsonCourse c) {
        // On vérifie d'abord si le cours existe (sécurité anti-doublon)
        return courseDao.findByEcoleAndCodeIgnoreCase(school, c.code())
                .orElseGet(() -> courseDao.save(KbCourse.builder()
                        .ecole(school)
                        .code(c.code())
                        .libelle(c.libelle())
                        .ects(c.ects())
                        // On génère une URL par défaut si pas spécifique
                        .urlProgramme(school.getUrlProgramme()) 
                        .build()));
    }

    private void createRule(KbSchool school, JsonRule r, Map<String, KbCourse> courseMap) {
        // 1. Instancier la règle
        KbCorrespondenceRule rule = KbCorrespondenceRule.builder()
                .ecole(school)
                .description(r.description())
                .minTotalEcts(r.minEcts())
                .build();

        // 2. Lier les SOURCES (Les cours ingrédients)
        // On utilise la Map 'courseMap' pour retrouver l'objet Java instantanément grâce à son code
        if (r.sources() != null) {
            r.sources().forEach(sourceCode -> {
                KbCourse course = courseMap.get(sourceCode);
                if (course != null) {
                    rule.addSource(KbCorrespondenceRuleSource.builder().rule(rule).cours(course).build());
                } else {
                    log.warn("⚠️ Cours source introuvable pour la règle '{}' : {}", r.description(), sourceCode);
                }
            });
        }

        // 3. Lier les CIBLES (Les UEs ISFCE)
        // On interroge la DB pour trouver l'UE par son code (ex: "IPAP")
        if (r.cibles() != null) {
            r.cibles().forEach(ueCode -> ueDao.findById(ueCode).ifPresentOrElse(
                    ue -> rule.addTarget(KbCorrespondenceRuleTarget.builder().rule(rule).ue(ue).build()),
                    () -> log.warn("⚠️ UE cible introuvable pour la règle '{}' : {}", r.description(), ueCode)
            ));
        }

        // 4. Sauvegarder (La cascade JPA s'occupera de sauvegarder les sources et targets)
        ruleDao.save(rule);
    }
}