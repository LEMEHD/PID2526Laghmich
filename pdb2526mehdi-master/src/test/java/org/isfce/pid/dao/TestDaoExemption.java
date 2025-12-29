package org.isfce.pid.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.isfce.pid.model.ExemptionRequest;
import org.isfce.pid.model.Section;
import org.isfce.pid.model.StatutDemande;
import org.isfce.pid.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("testU") // Utilise la config H2 de test
@SpringBootTest
class TestDaoExemption {

    @Autowired
    private IExemptionRequestDao exemptionRequestDao;

    @Autowired
    private IStudentDao studentDao;

    @Autowired
    private ISectionDao sectionDao;

    private Student studentTest;
    private Section sectionTest;

    @BeforeEach
    void setUp() {
        // 1. On prépare la section
        sectionTest = new Section("INFO", "Informatique de Gestion", null);
       
        // On capture le retour du save
        // Hibernate retourne l'instance "connectée" à la base de données.
        sectionTest = sectionDao.save(sectionTest);

        // 2. On prépare l'étudiant
        studentTest = Student.builder()
                .email("etudiant.test@isfce.be")
                .nom("Test")
                .prenom("Jean")
                .section(sectionTest) // On utilise bien la section connectée
                .build();
        
        studentTest = studentDao.save(studentTest);
    }

    @Test
    @Transactional
    void testSaveAndGetExemptionRequest() {
        // ——— ARRANGEMENT ———
        ExemptionRequest request = ExemptionRequest.builder()
                .etudiant(studentTest)
                .section(sectionTest)
                .statut(StatutDemande.DRAFT)
                .build();

        // ——— ACTION ———
        ExemptionRequest savedReq = exemptionRequestDao.save(request);
        UUID reqId = savedReq.getId();

        // ——— ASSERTION (Vérification de base) ———
        assertNotNull(reqId, "L'ID ne doit pas être null après sauvegarde");
        
        // Vérification de la récupération simple
        Optional<ExemptionRequest> retrieved = exemptionRequestDao.findById(reqId);
        assertTrue(retrieved.isPresent());
        assertEquals(StatutDemande.DRAFT, retrieved.get().getStatut());
        assertEquals("etudiant.test@isfce.be", retrieved.get().getEtudiant().getEmail());
    }

    @Test
    @Transactional
    void testFindWithAllById() {
        // Ce test vérifie spécifiquement ton @EntityGraph
        
        // 1. Création de la demande
        ExemptionRequest request = ExemptionRequest.builder()
                .etudiant(studentTest)
                .section(sectionTest)
                .statut(StatutDemande.DRAFT)
                .build();
        exemptionRequestDao.save(request);
        
        // 2. Appel de la méthode optimisée
        Optional<ExemptionRequest> result = exemptionRequestDao.findWithAllById(request.getId());

        // 3. Vérifications
        assertTrue(result.isPresent());
        assertNotNull(result.get().getExternalCourses(), "La liste des cours externes doit être initialisée (même vide)");
        assertNotNull(result.get().getItems(), "La liste des items doit être initialisée");
        
        // Vérifie qu'on a bien l'objet complet
        assertEquals(studentTest.getNom(), result.get().getEtudiant().getNom());
    }
    
    @Test
    @Transactional
    void testDeleteRequestCascades() {
        // Vérifie que si on supprime une demande, ça ne plante pas
        ExemptionRequest request = ExemptionRequest.builder()
                .etudiant(studentTest)
                .section(sectionTest)
                .build();
        request = exemptionRequestDao.save(request);
        UUID id = request.getId();

        exemptionRequestDao.deleteById(id);

        assertFalse(exemptionRequestDao.findById(id).isPresent());
        // L'étudiant ne doit PAS être supprimé (vérification de la non-cascade sur le parent)
        assertTrue(studentDao.findByEmail("etudiant.test@isfce.be").isPresent());
    }
}