package org.isfce.pid.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.isfce.pid.dao.IKbCorrespondenceRuleDao;
import org.isfce.pid.dao.IKbCourseDao;
import org.isfce.pid.dao.IKbSchoolDao;
import org.isfce.pid.model.ExternalCourse;
import org.isfce.pid.model.KbCorrespondenceRule;
import org.isfce.pid.model.KbCorrespondenceRuleSource;
import org.isfce.pid.model.KbCourse;
import org.isfce.pid.model.KbSchool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service implémentant le moteur de règles académiques (Rule Engine).
 * Compare les cours externes encodés par l'étudiant avec la Base de Connaissances (KB)
 * pour identifier automatiquement les dispenses applicables.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KnowledgeBaseService {

    private final IKbSchoolDao kbSchoolDao;
    private final IKbCourseDao kbCourseDao;
    private final IKbCorrespondenceRuleDao kbRuleDao;

    /**
     * DTO interne représentant une correspondance validée par le moteur.
     *
     * @param rule           La règle de correspondance satisfaite.
     * @param studentCourses Les cours de l'étudiant ayant permis de valider cette règle.
     */
    public record RuleMatch(KbCorrespondenceRule rule, Set<ExternalCourse> studentCourses) {
    }

    /**
     * Analyse les cours soumis par l'étudiant pour détecter les règles de dispenses applicables.
     *
     * @param studentCourses Ensemble des cours externes encodés par l'étudiant.
     * @return Une liste de correspondances (Règle + Cours justificatifs).
     */
    public List<RuleMatch> findMatchingRules(Set<ExternalCourse> studentCourses) {
        // Associer les cours de l'étudiant aux cours connus de la KB
        Map<KbCourse, List<ExternalCourse>> recognizedCourses = mapStudentCoursesToKb(studentCourses);

        if (recognizedCourses.isEmpty()) {
            return List.of();
        }

        // Identifier les écoles concernées pour optimiser la recherche (éviter de charger toutes les règles)
        Set<KbSchool> involvedSchools = recognizedCourses.keySet().stream()
                .map(KbCourse::getEcole)
                .collect(Collectors.toSet());

        List<RuleMatch> matches = new ArrayList<>();

        // Vérification des règles par école
        for (KbSchool school : involvedSchools) {
            List<KbCorrespondenceRule> schoolRules = kbRuleDao.findByEcole(school);

            for (KbCorrespondenceRule rule : schoolRules) {
                Set<ExternalCourse> usedCourses = getMatchingCoursesIfSatisfied(rule, recognizedCourses);

                if (!usedCourses.isEmpty()) {
                    matches.add(new RuleMatch(rule, usedCourses));
                }
            }
        }

        return matches;
    }

    /**
     * Récupère la liste complète des écoles référencées.
     *
     * @return Liste des écoles.
     */
    public List<KbSchool> getAllSchools() {
        return kbSchoolDao.findAll();
    }

    /**
     * Recherche un cours spécifique dans la base de connaissances.
     *
     * @param schoolCode Code de l'école (ex: "ULB").
     * @param courseCode Code du cours (ex: "INFO-F101").
     * @return Le cours correspondant s'il existe.
     */
    public Optional<KbCourse> findCourse(String schoolCode, String courseCode) {
        return kbSchoolDao.findByCodeIgnoreCase(schoolCode)
                .flatMap(school -> kbCourseDao.findByEcoleAndCodeIgnoreCase(school, courseCode));
    }

    // ————— MÉTHODES PRIVÉES (Moteur de règles) —————

    /**
     * Vérifie si une règle est satisfaite en fonction des cours reconnus.
     * Valide la présence des cours requis et le total des crédits ECTS.
     */
    private Set<ExternalCourse> getMatchingCoursesIfSatisfied(KbCorrespondenceRule rule, Map<KbCourse, List<ExternalCourse>> recognizedCourses) {
        Set<ExternalCourse> foundCourses = new HashSet<>();

        // 1. Vérifier la présence de tous les cours sources requis
        for (KbCorrespondenceRuleSource source : rule.getSources()) {
            List<ExternalCourse> studentMatches = recognizedCourses.get(source.getCours());

            if (studentMatches == null || studentMatches.isEmpty()) {
                return Collections.emptySet(); // Règle échouée
            }

            foundCourses.addAll(studentMatches);
        }

        // 2. Vérifier le minimum de crédits ECTS (si applicable)
        if (rule.getMinTotalEcts() != null && rule.getMinTotalEcts() > 0) {
            // On se base sur les ECTS officiels de la KB (plus fiable que ceux déclarés par l'élève)
            int totalEctsKb = rule.getSources().stream()
                    .mapToInt(src -> src.getCours().getEcts())
                    .sum();

            if (totalEctsKb < rule.getMinTotalEcts()) {
                return Collections.emptySet();
            }
        }

        return foundCourses;
    }

    /**
     * Mappe les cours déclarés par l'étudiant vers les cours officiels de la KB.
     * La correspondance se fait sur le couple (Nom École, Code Cours).
     */
    private Map<KbCourse, List<ExternalCourse>> mapStudentCoursesToKb(Set<ExternalCourse> studentCourses) {
        Map<KbCourse, List<ExternalCourse>> map = new HashMap<>();

        for (ExternalCourse ext : studentCourses) {
            // Optimisation : Utilisation des optionals en chaîne pour éviter les if imbriqués
            kbSchoolDao.findByCodeIgnoreCase(ext.getEtablissement())
                    .flatMap(school -> kbCourseDao.findByEcoleAndCodeIgnoreCase(school, ext.getCode()))
                    .ifPresent(kbCourse -> map.computeIfAbsent(kbCourse, k -> new ArrayList<>()).add(ext));
        }
        return map;
    }
}