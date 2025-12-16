package org.isfce.pid.service;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dao.IKbCorrespondenceRuleDao;
import org.isfce.pid.dao.IKbCourseDao;
import org.isfce.pid.dao.IKbSchoolDao;
import org.isfce.pid.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service gérant le "Moteur de Règles" académiques.
 * Il est chargé de comparer les cours encodés par l'étudiant avec la base de connaissances (KB)
 * pour identifier automatiquement les dispenses potentielles.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimisé pour la lecture
public class KnowledgeBaseService {

    private final IKbSchoolDao kbSchoolDao;
    private final IKbCourseDao kbCourseDao;
    private final IKbCorrespondenceRuleDao kbRuleDao;

    /**
     * DTO interne (Record) représentant une correspondance trouvée ("Match").
     * Contient la règle validée et la liste exacte des cours de l'étudiant qui ont servi à la valider.
     * * @param rule La règle de correspondance satisfaite.
     * @param studentCourses Les cours externes de l'étudiant qui satisfont cette règle.
     */
    public record RuleMatch(KbCorrespondenceRule rule, Set<ExternalCourse> studentCourses) {}

    /**
     * Analyse un ensemble de cours externes soumis par un étudiant et identifie
     * toutes les règles de correspondance applicables.
     *
     * @param studentCourses Le set des cours encodés par l'étudiant.
     * @return La liste des règles satisfaites, accompagnées des cours justificatifs (RuleMatch).
     */
    public List<RuleMatch> findMatchingRules(Set<ExternalCourse> studentCourses) {
        // 1. Mapping : Associer les cours de l'étudiant aux cours connus de la KB
        // Structure : Map<CoursOfficiel_KB, Liste<Cours_Encodés_Par_Etudiant>>
        Map<KbCourse, List<ExternalCourse>> recognizedCourses = mapStudentCoursesToKb(studentCourses);

        if (recognizedCourses.isEmpty()) {
            return List.of(); // Rien de connu, aucune règle ne peut s'appliquer
        }

        // 2. Identification des écoles concernées pour optimiser la recherche de règles
        Set<KbSchool> involvedSchools = recognizedCourses.keySet().stream()
                .map(KbCourse::getEcole)
                .collect(Collectors.toSet());

        List<RuleMatch> matches = new ArrayList<>();

        // 3. Vérification des règles par école
        for (KbSchool school : involvedSchools) {
            // On charge toutes les règles connues pour cette école
            List<KbCorrespondenceRule> schoolRules = kbRuleDao.findByEcole(school);

            for (KbCorrespondenceRule rule : schoolRules) {
                // On vérifie si la règle est satisfaite et on récupère les ingrédients utilisés
                Set<ExternalCourse> usedCourses = getMatchingCoursesIfSatisfied(rule, recognizedCourses);
                
                if (!usedCourses.isEmpty()) {
                    matches.add(new RuleMatch(rule, usedCourses));
                }
            }
        }

        return matches;
    }

    /**
     * Vérifie si une règle spécifique est satisfaite par les cours reconnus.
     *
     * @param rule La règle à tester.
     * @param recognizedCourses La map des cours de l'étudiant déjà identifiés.
     * @return Un Set contenant les ExternalCourse utilisés si la règle est valide, sinon un Set vide.
     */
    private Set<ExternalCourse> getMatchingCoursesIfSatisfied(KbCorrespondenceRule rule, Map<KbCourse, List<ExternalCourse>> recognizedCourses) {
        Set<ExternalCourse> foundCourses = new HashSet<>();

        // A. Vérifier la PRÉSENCE de tous les ingrédients requis (Sources)
        for (KbCorrespondenceRuleSource source : rule.getSources()) {
            List<ExternalCourse> studentMatches = recognizedCourses.get(source.getCours());
            
            // Si l'étudiant n'a pas ce cours spécifique -> Règle échouée
            if (studentMatches == null || studentMatches.isEmpty()) {
                return Collections.emptySet(); 
            }
            
            // On ajoute tous les candidats correspondants (au cas où l'étudiant a encodé 2x le même cours)
            foundCourses.addAll(studentMatches);
        }

        // B. Vérifier les CRÉDITS (ECTS) si la règle impose un minimum
        // Exemple: "Il faut avoir réussi 8 ECTS de prog pour dispenser IPAP"
        if (rule.getMinTotalEcts() != null && rule.getMinTotalEcts() > 0) {
            // On se base sur les ECTS officiels de la KB (plus fiable que ceux déclarés par l'élève)
            int totalEctsKb = rule.getSources().stream()
                    .mapToInt(src -> src.getCours().getEcts())
                    .sum();
            
            if (totalEctsKb < rule.getMinTotalEcts()) {
                return Collections.emptySet(); // Pas assez de crédits -> Règle échouée
            }
        }

        return foundCourses;
    }

    /**
     * Tente de lier chaque ExternalCourse encodé par l'étudiant à un KbCourse existant en base.
     * La correspondance se fait sur base du Code Établissement + Code Cours.
     */
    private Map<KbCourse, List<ExternalCourse>> mapStudentCoursesToKb(Set<ExternalCourse> studentCourses) {
        Map<KbCourse, List<ExternalCourse>> map = new HashMap<>();

        for (ExternalCourse ext : studentCourses) {
            // Recherche de l'école (par code, ex: "ULB")
            kbSchoolDao.findByCodeIgnoreCase(ext.getEtablissement()).ifPresent(school -> {
                // Recherche du cours dans cette école (par code, ex: "INFO-F101")
                kbCourseDao.findByEcoleAndCodeIgnoreCase(school, ext.getCode()).ifPresent(kbCourse -> {
                    // Si trouvé, on l'ajoute à la map
                    map.computeIfAbsent(kbCourse, k -> new ArrayList<>()).add(ext);
                });
            });
        }
        return map;
    }

    // ————— UTILITAIRES (Pour les listes déroulantes Frontend) —————

    public List<KbSchool> getAllSchools() {
        return kbSchoolDao.findAll();
    }
    
    public Optional<KbCourse> findCourse(String schoolCode, String courseCode) {
         return kbSchoolDao.findByCodeIgnoreCase(schoolCode)
                 .flatMap(school -> kbCourseDao.findByEcoleAndCodeIgnoreCase(school, courseCode));
    }
}