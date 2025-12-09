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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimisé pour la lecture
public class KnowledgeBaseService {

    private final IKbSchoolDao kbSchoolDao;
    private final IKbCourseDao kbCourseDao;
    private final IKbCorrespondenceRuleDao kbRuleDao;

    /**
     * Analyse un ensemble de cours externes soumis par un étudiant et identifie
     * toutes les règles de correspondance applicables (dispenses automatiques).
     *
     * @param studentCourses Le set des cours encodés par l'étudiant
     * @return La liste des règles de la base de connaissances qui sont satisfaites
     */
    public List<KbCorrespondenceRule> findMatchingRules(Set<ExternalCourse> studentCourses) {
        // 1. Mapping : Associer les cours de l'étudiant aux cours connus de la KB
        // Map<CoursConnu, Liste<DeclarationsEtudiant>>
        Map<KbCourse, List<ExternalCourse>> recognizedCourses = mapStudentCoursesToKb(studentCourses);

        if (recognizedCourses.isEmpty()) {
            return List.of(); // Rien de connu, aucune règle ne peut s'appliquer
        }

        // 2. Identification des écoles concernées pour limiter la recherche
        Set<KbSchool> involvedSchools = recognizedCourses.keySet().stream()
                .map(KbCourse::getEcole)
                .collect(Collectors.toSet());

        List<KbCorrespondenceRule> applicableRules = new ArrayList<>();

        // 3. Vérification des règles par école
        for (KbSchool school : involvedSchools) {
            List<KbCorrespondenceRule> schoolRules = kbRuleDao.findByEcole(school);

            for (KbCorrespondenceRule rule : schoolRules) {
                if (isRuleSatisfied(rule, recognizedCourses)) {
                    applicableRules.add(rule);
                }
            }
        }

        return applicableRules;
    }

    /**
     * Helper : Tente de lier chaque ExternalCourse à un KbCourse existant.
     */
    private Map<KbCourse, List<ExternalCourse>> mapStudentCoursesToKb(Set<ExternalCourse> studentCourses) {
        Map<KbCourse, List<ExternalCourse>> map = new HashMap<>();

        for (ExternalCourse ext : studentCourses) {
            // Recherche de l'école (par code, ex: "ULB")
            kbSchoolDao.findByCodeIgnoreCase(ext.getEtablissement()).ifPresent(school -> {
                // Recherche du cours dans cette école (par code, ex: "INFO-F101")
                kbCourseDao.findByEcoleAndCodeIgnoreCase(school, ext.getCode()).ifPresent(kbCourse -> {
                    map.computeIfAbsent(kbCourse, k -> new ArrayList<>()).add(ext);
                });
            });
        }
        return map;
    }

    /**
     * Helper : Vérifie si une règle précise est satisfaite par les cours reconnus.
     */
    private boolean isRuleSatisfied(KbCorrespondenceRule rule, Map<KbCourse, List<ExternalCourse>> recognizedCourses) {
        // A. Vérifier la PRÉSENCE de tous les cours requis (Sources)
        // On récupère la liste des KbCourse nécessaires pour cette règle
        Set<KbCourse> requiredCourses = rule.getSources().stream()
                .map(KbCorrespondenceRuleSource::getCours)
                .collect(Collectors.toSet());

        // Est-ce que l'étudiant a TOUS ces cours dans son map ?
        boolean allSourcesPresent = requiredCourses.stream().allMatch(recognizedCourses::containsKey);

        if (!allSourcesPresent) {
            return false;
        }

        // B. Vérifier les CRÉDITS (ECTS) si la règle impose un minimum
        if (rule.getMinTotalEcts() != null && rule.getMinTotalEcts() > 0) {
            int totalEcts = requiredCourses.stream()
                    .mapToInt(KbCourse::getEcts) // On prend les ECTS officiels de la KB, pas ceux déclarés par l'étudiant (plus sûr)
                    .sum();
            
            if (totalEcts < rule.getMinTotalEcts()) {
                return false;
            }
        }

        return true;
    }

    // --- Méthodes utilitaires pour le Frontend ---

    public List<KbSchool> getAllSchools() {
        return kbSchoolDao.findAll();
    }
    
    public Optional<KbCourse> findCourse(String schoolCode, String courseCode) {
         return kbSchoolDao.findByCodeIgnoreCase(schoolCode)
                 .flatMap(school -> kbCourseDao.findByEcoleAndCodeIgnoreCase(school, courseCode));
    }
}