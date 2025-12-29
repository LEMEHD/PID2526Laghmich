package org.isfce.pid.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.isfce.pid.dao.*;
import org.isfce.pid.dto.*;
import org.isfce.pid.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("testU")
@SpringBootTest
class TestExemptionService {

    @Autowired
    private ExemptionService exemptionService;

    // ——— MOCKS (Simulateurs) ———
    @MockitoBean private IExemptionRequestDao reqDao;
    @MockitoBean private ISectionDao sectionDao;
    @MockitoBean private StudentService studentService;
    @MockitoBean private KnowledgeBaseService kbService; // Le cerveau
    @MockitoBean private IExemptionItemDao itemDao;
    @MockitoBean private IExternalCourseDao extCourseDao;
    @MockitoBean private IUeDao ueDao;
    @MockitoBean private ISupportingDocumentDao docDao;

    // ——— DONNÉES DE TEST ———
    private UUID reqId;
    private ExemptionRequest draftReq;
    private Student student;
    private Section section;

    @BeforeEach
    void setUp() {
        reqId = UUID.randomUUID();
        section = new Section("INFO", "Informatique", null);
        student = Student.builder().email("jean@test.be").nom("Dupont").prenom("Jean").build();

        // Une demande de base propre pour chaque test
        draftReq = ExemptionRequest.builder()
                .etudiant(student)
                .section(section)
                .statut(StatutDemande.DRAFT)
                .externalCourses(new HashSet<>())
                .items(new HashSet<>())
                .globalDocuments(new HashSet<>())
                .build();
        draftReq.setId(reqId);
    }

    // ===================================================================================
    // 1. TESTS DE CRÉATION & AJOUTS SIMPLES (Happy Path)
    // ===================================================================================

    @Test
    @DisplayName("Création d'un brouillon : Succès")
    void testCreateDraft_Success() {
        CreateExemptionRequestDto dto = new CreateExemptionRequestDto();
        dto.setEmail("jean@test.be");
        dto.setSectionCode("INFO");

        when(sectionDao.findById("INFO")).thenReturn(Optional.of(section));
        when(studentService.getOrCreateByEmail("jean@test.be")).thenReturn(student);
        when(reqDao.save(any(ExemptionRequest.class))).thenAnswer(i -> {
            ExemptionRequest r = i.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ExemptionRequestDto result = exemptionService.createDraft(dto);

        assertNotNull(result.getId());
        assertEquals(StatutDemande.DRAFT, result.getStatut());
        verify(reqDao).save(any());
    }

    @Test
    @DisplayName("Ajout d'un cours externe : Succès")
    void testAddExternalCourse_Success() {
        AddExternalCourseDto dto = new AddExternalCourseDto();
        dto.setEtablissement("ULB");
        dto.setCode("JAVA");
        dto.setLibelle("Java OO");
        dto.setEcts(5);

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));
        when(extCourseDao.save(any(ExternalCourse.class))).thenAnswer(i -> {
            ExternalCourse c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        ExternalCourseDto result = exemptionService.addExternalCourse(reqId, dto);

        assertEquals("ULB", result.getEtablissement());
        assertEquals("JAVA", result.getCode());
        verify(extCourseDao).save(any());
    }

    @Test
    @DisplayName("Ajout d'un document global : Succès")
    void testAddGlobalDocument_Success() {
        AddSupportingDocumentDto dto = new AddSupportingDocumentDto();
        dto.setType(TypeDocument.BULLETIN);
        dto.setUrlStockage("http://srv/file.pdf");
        dto.setOriginalFileName("bulletin.pdf");

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));
        when(docDao.save(any(SupportingDocument.class))).thenAnswer(i -> {
            SupportingDocument d = i.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        SupportingDocumentDto result = exemptionService.addGlobalDocument(reqId, dto);

        assertEquals(TypeDocument.BULLETIN, result.getType());
        verify(docDao).save(any());
    }

    // ===================================================================================
    // 2. TESTS DU MOTEUR DE RÈGLES (KnowledgeBase)
    // ===================================================================================

    @Test
    @DisplayName("Analyse Auto : Cas Complexe (2 cours externes -> 1 UE Interne)")
    void testAnalyzeRequest_ComplexRule_ManyToOne() {
        // SCENARIO : L'étudiant a "Algo 1" (4 ECTS) et "Algo 2" (4 ECTS).
        // La règle dit : "Algo 1 + Algo 2 = IPAP (8 ECTS)".

        // 1. Préparer les cours de l'étudiant
        ExternalCourse algo1 = ExternalCourse.builder().code("ALGO1").ects(4).request(draftReq).build();
        ExternalCourse algo2 = ExternalCourse.builder().code("ALGO2").ects(4).request(draftReq).build();
        draftReq.getExternalCourses().addAll(Set.of(algo1, algo2));

        // 2. Préparer la règle complexe (Simulée venant du KBService)
        UE ueCible = UE.builder().code("IPAP").nom("Programmation").ects(8).build();
        KbCorrespondenceRule rule = KbCorrespondenceRule.builder()
                .description("Règle Combinée")
                .targets(Set.of(KbCorrespondenceRuleTarget.builder().ue(ueCible).build()))
                .build();

        // Le KB Service renvoie un match qui lie la règle aux DEUX cours
        KnowledgeBaseService.RuleMatch match = new KnowledgeBaseService.RuleMatch(
                rule,
                Set.of(algo1, algo2) // Les preuves
        );

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));
        when(kbService.findMatchingRules(any())).thenReturn(List.of(match));
        when(reqDao.save(any())).thenReturn(draftReq); // Retourne l'objet modifié

        // ACTION
        ExemptionRequestFullDto result = exemptionService.analyzeRequest(reqId);

        // ASSERTION
        assertEquals(1, result.getItems().size(), "Une seule dispense doit être créée");
        
        ExemptionItemDto item = result.getItems().iterator().next();
        assertEquals("IPAP", item.getUe().getCode());
        assertEquals(DecisionItem.AUTO_ACCEPTED, item.getDecision());
        
        // Vérification CRITIQUE : L'item est-il bien justifié par les 2 cours ?
        assertEquals(2, item.getJustifyingCourses().size(), "La dispense doit être liée aux 2 cours externes");
        verify(itemDao).save(any(ExemptionItem.class));
    }

    // ===================================================================================
    // 3. TESTS DE VALIDATION À LA SOUMISSION (Les Garde-fous)
    // ===================================================================================

    @Test
    @DisplayName("Soumission : Échec si cours orphelins (non utilisés)")
    void testSubmitRequest_Throws_WhenOrphanCourses() {
        // SCENARIO : L'étudiant ajoute un cours "Cuisine" mais ne demande aucune dispense avec.
        
        ExternalCourse courseOrphan = ExternalCourse.builder().code("CUISINE").request(draftReq).build();
        courseOrphan.setId(UUID.randomUUID());
        draftReq.getExternalCourses().add(courseOrphan);
        
        // On simule qu'il y a une autre dispense valide pour ne pas échouer sur "liste vide"
        ExternalCourse courseValid = ExternalCourse.builder().code("JAVA").request(draftReq).build();
        courseValid.setId(UUID.randomUUID());
        draftReq.getExternalCourses().add(courseValid);
        
        ExemptionItem itemValid = ExemptionItem.builder()
                .request(draftReq)
                .justifyingCourses(Set.of(courseValid)) // Seul JAVA est utilisé
                .build();
        draftReq.getItems().add(itemValid);

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));

        // ACTION & ASSERTION
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> exemptionService.submitRequest(reqId));
        assertTrue(ex.getMessage().contains("orphelins"), 
                "Le message d'erreur devrait mentionner les cours orphelins");
    }

    @Test
    @DisplayName("Soumission : Échec si dossier vide (pas de cours)")
    void testSubmitRequest_Throws_WhenEmpty() {
        // Dossier vierge
        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));

        assertThrows(IllegalStateException.class, () -> exemptionService.submitRequest(reqId));
    }

    @Test
    @DisplayName("Soumission : Échec si preuves manquantes")
    void testSubmitRequest_Throws_WhenMissingProofs() {
        // SCENARIO : Cours encodé, dispense demandée, MAIS aucun document (ni global, ni spécifique).
        
        ExternalCourse c1 = ExternalCourse.builder().code("JAVA").request(draftReq).documents(new HashSet<>()).build();
        c1.setId(UUID.randomUUID());
        draftReq.getExternalCourses().add(c1);

        ExemptionItem item = ExemptionItem.builder().justifyingCourses(Set.of(c1)).request(draftReq).build();
        draftReq.getItems().add(item);

        // Pas de documents globaux
        draftReq.setGlobalDocuments(new HashSet<>());

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));

        // ACTION & ASSERTION
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> exemptionService.submitRequest(reqId));
        
        // On vérifie que le message d'erreur contient bien le mot clé attendu
        assertTrue(ex.getMessage().contains("Documents") || ex.getMessage().contains("manquants"), 
                "Le message d'erreur devrait mentionner les documents manquants. Reçu: " + ex.getMessage());
        }

    @Test
    @DisplayName("Soumission : Succès (Happy Path)")
    void testSubmitRequest_Success() {
        // SCENARIO : Tout est OK.
        
        // 1. Cours + Document spécifique
        ExternalCourse c1 = ExternalCourse.builder().code("JAVA").request(draftReq).documents(new HashSet<>()).build();
        c1.setId(UUID.randomUUID());
        c1.getDocuments().add(SupportingDocument.builder().type(TypeDocument.PROGRAMME).build()); // Preuve OK
        
        draftReq.getExternalCourses().add(c1);

        // 2. Dispense utilisant le cours
        ExemptionItem item = ExemptionItem.builder().justifyingCourses(Set.of(c1)).request(draftReq).build();
        draftReq.getItems().add(item);

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));
        when(reqDao.save(any())).thenAnswer(i -> i.getArgument(0)); // Renvoie l'objet sauvegardé

        // ACTION
        ExemptionRequestFullDto result = exemptionService.submitRequest(reqId);

        // ASSERTION
        assertEquals(StatutDemande.SUBMITTED, result.getStatut());
        verify(reqDao).save(draftReq);
    }

    // ===================================================================================
    // 4. TESTS DE SÉCURITÉ & VERROUILLAGE
    // ===================================================================================

    @Test
    @DisplayName("Sécurité : Refus d'utiliser le cours d'un autre étudiant")
    void testAddManualItem_Throws_SecurityException() {
        // SCENARIO : On tente de lier un cours appartenant à une autre Request ID.
        
        ExternalCourse stolenCourse = ExternalCourse.builder().code("VOLÉ").build();
        ExemptionRequest otherReq = new ExemptionRequest();
        otherReq.setId(UUID.randomUUID()); // ID Différent !
        stolenCourse.setRequest(otherReq);
        stolenCourse.setId(UUID.randomUUID());

        AddManualExemptionItemDto dto = new AddManualExemptionItemDto();
        dto.setUeCode("IPAP");
        dto.setExternalCourseIds(List.of(stolenCourse.getId()));

        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));
        when(ueDao.findById("IPAP")).thenReturn(Optional.of(UE.builder().code("IPAP").ects(5).build()));
        when(extCourseDao.findAllById(any())).thenReturn(List.of(stolenCourse));

        // ACTION & ASSERTION
        assertThrows(SecurityException.class, () -> exemptionService.addManualItem(reqId, dto));
    }

    @Test
    @DisplayName("Verrouillage : Modification interdite après soumission")
    void testModification_Throws_WhenSubmitted() {
        // Le dossier est verrouillé
        draftReq.setStatut(StatutDemande.SUBMITTED);
        when(reqDao.findById(reqId)).thenReturn(Optional.of(draftReq));

        // Tentative d'ajout de cours
        AddExternalCourseDto dto = new AddExternalCourseDto();
        assertThrows(IllegalStateException.class, () -> exemptionService.addExternalCourse(reqId, dto));

        // Tentative d'analyse
        assertThrows(IllegalStateException.class, () -> exemptionService.analyzeRequest(reqId));
        
        // On vérifie qu'aucun DAO de sauvegarde n'a été appelé
        verify(extCourseDao, never()).save(any());
    }

    @Test
    @DisplayName("Suppression : Item manuel")
    void testDeleteItem_Success() {
        UUID itemId = UUID.randomUUID();
        ExemptionItem item = ExemptionItem.builder().request(draftReq).build();
        item.setId(itemId);
        draftReq.getItems().add(item);

        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(reqDao.save(any())).thenReturn(draftReq);

        exemptionService.deleteItem(itemId);

        assertFalse(draftReq.getItems().contains(item));
        verify(itemDao).delete(item);
    }
}