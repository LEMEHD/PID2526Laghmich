package org.isfce.pid.seed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.isfce.pid.dao.IKbCorrespondenceRuleDao;
import org.isfce.pid.dao.IKbCourseDao;
import org.isfce.pid.dao.IKbSchoolDao;
import org.isfce.pid.dao.IUeDao;
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
 * Composant de démarrage chargé d'initialiser la Base de Connaissances (Knowledge Base).
 * Ce chargeur lit un fichier JSON de configuration au lancement de l'application
 * pour peupler la base de données avec les écoles, cours et règles de correspondance par défaut.
 */
@Slf4j
@Component
@Profile("!testU")
@RequiredArgsConstructor
public class KnowledgeBaseLoader implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final IKbSchoolDao schoolDao;
    private final IKbCourseDao courseDao;
    private final IKbCorrespondenceRuleDao ruleDao;
    private final IUeDao ueDao;

    // Records pour le mapping JSON
    record JsonCourse(String code, String libelle, int ects) {
    }

    record JsonRule(String description, int minEcts, List<String> sources, List<String> cibles) {
    }

    record JsonSchool(String code, String nom, String site, List<JsonCourse> cours, List<JsonRule> regles) {
    }

    /**
     * Exécute le chargement des données au démarrage de l'application.
     * Vérifie d'abord si la base est vide pour éviter d'écraser des données existantes.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    @Override
    @Transactional
    public void run(String... args) {
        if (schoolDao.count() > 0) {
            log.info("ℹ️ Base de connaissances déjà remplie. Le loader JSON est ignoré.");
            return;
        }

        try {
            log.info("🚀 Chargement de la Base de Connaissances depuis knowledge-base.json...");

            InputStream inputStream = new ClassPathResource("knowledge-base.json").getInputStream();
            List<JsonSchool> schoolsData = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            for (JsonSchool schoolData : schoolsData) {
                processSchool(schoolData);
            }

            log.info("✅ Base de Connaissances initialisée avec succès !");

        } catch (Exception e) {
            log.error("❌ Erreur critique lors du chargement de la Knowledge Base", e);
        }
    }

    /**
     * Traite les données d'une école, incluant ses cours et ses règles.
     */
    private void processSchool(JsonSchool data) {
        KbSchool school = schoolDao.findByCodeIgnoreCase(data.code())
                .orElseGet(() -> schoolDao.save(KbSchool.builder()
                        .code(data.code())
                        .etablissement(data.nom())
                        .urlProgramme(data.site())
                        .build()));

        log.info("   -> École chargée : {}", school.getEtablissement());

        Map<String, KbCourse> courseMap = data.cours().stream()
                .map(c -> createCourse(school, c))
                .collect(Collectors.toMap(KbCourse::getCode, Function.identity()));

        if (data.regles() != null) {
            for (JsonRule ruleData : data.regles()) {
                createRule(school, ruleData, courseMap);
            }
        }
    }

    /**
     * Crée ou récupère un cours externe associé à une école.
     */
    private KbCourse createCourse(KbSchool school, JsonCourse c) {
        return courseDao.findByEcoleAndCodeIgnoreCase(school, c.code())
                .orElseGet(() -> courseDao.save(KbCourse.builder()
                        .ecole(school)
                        .code(c.code())
                        .libelle(c.libelle())
                        .ects(c.ects())
                        .urlProgramme(school.getUrlProgramme())
                        .build()));
    }

    /**
     * Crée une règle de correspondance entre des cours externes et des UEs internes.
     */
    private void createRule(KbSchool school, JsonRule r, Map<String, KbCourse> courseMap) {
        KbCorrespondenceRule rule = KbCorrespondenceRule.builder()
                .ecole(school)
                .description(r.description())
                .minTotalEcts(r.minEcts())
                .build();

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

        if (r.cibles() != null) {
            r.cibles().forEach(ueCode -> ueDao.findById(ueCode).ifPresentOrElse(
                    ue -> rule.addTarget(KbCorrespondenceRuleTarget.builder().rule(rule).ue(ue).build()),
                    () -> log.warn("⚠️ UE cible introuvable pour la règle '{}' : {}", r.description(), ueCode)
            ));
        }

        ruleDao.save(rule);
    }
}