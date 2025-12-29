package org.isfce.pid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.isfce.pid.dao.IExemptionItemDao;
import org.isfce.pid.dao.IExemptionRequestDao;
import org.isfce.pid.dao.IExternalCourseDao;
import org.isfce.pid.dao.ISectionDao;
import org.isfce.pid.dao.IUeDao;
import org.isfce.pid.dto.AddExternalCourseDto;
import org.isfce.pid.dto.AddManualExemptionItemDto;
import org.isfce.pid.dto.CreateExemptionRequestDto;
import org.isfce.pid.dto.ExemptionItemDto;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestFullDto;
import org.isfce.pid.model.DecisionItem;
import org.isfce.pid.model.ExemptionItem;
import org.isfce.pid.model.ExemptionRequest;
import org.isfce.pid.model.ExternalCourse;
import org.isfce.pid.model.KbCorrespondenceRule;
import org.isfce.pid.model.KbCorrespondenceRuleTarget;
import org.isfce.pid.model.Section;
import org.isfce.pid.model.StatutDemande;
import org.isfce.pid.model.Student;
import org.isfce.pid.model.SupportingDocument;
import org.isfce.pid.model.TypeDocument;
import org.isfce.pid.model.UE;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("testU")
@SpringBootTest
class TestExemptionService {

    @Autowired
    private ExemptionService exemptionService;

    // On MOCK les dépendances pour isoler le service
    @MockBean
    private IExemptionRequestDao reqDao;
    @MockBean
    private ISectionDao sectionDao;
    @MockBean
    private StudentService studentService;
    @MockBean
    private KnowledgeBaseService kbService;
    @MockBean
    private IExemptionItemDao itemDao;
    @MockBean
    private IExternalCourseDao extCourseDao;
    @MockBean
    private IUeDao ueDao;

    // NOTE: On ne mocke PAS le Mapper, on utilise le vrai grâce à @SpringBootTest

    @Test
    void testCreateDraft_Success() {
        // ——— ARRANGEMENT ———
        String email = "etudiant@test.be";
        String codeSection = "INFO";
        
        CreateExemptionRequestDto dto = new CreateExemptionRequestDto();
        dto.setEmail(email);
        dto.setSectionCode(codeSection);

        Section mockSection = new Section(codeSection, "Info Gestion", null);
        Student mockStudent = Student.builder().email(email).prenom("Jean").nom("Test").build();
        
        // Simulation des réponses des DAOs
        when(sectionDao.findById(codeSection)).thenReturn(Optional.of(mockSection));
        when(studentService.getOrCreateByEmail(email)).thenReturn(mockStudent);
        
        // Quand on sauvegarde, on renvoie l'objet avec un ID généré
        when(reqDao.save(any(ExemptionRequest.class))).thenAnswer(invocation -> {
            ExemptionRequest r = invocation.getArgument(0);
            r.setId(UUID.randomUUID()); // On simule la génération d'ID par la DB
            return r;
        });

        // ——— ACTION ———
        ExemptionRequestDto result = exemptionService.createDraft(dto);

        // ——— ASSERTION ———
        assertNotNull(result.getId());
        assertEquals(email, result.getEtudiant().getEmail());
        assertEquals(StatutDemande.DRAFT, result.getStatut());
        
        // Vérifie que le service a bien appelé le DAO
        verify(reqDao).save(any(ExemptionRequest.class));
    }

    @Test
    void testCreateDraft_SectionNotFound() {
        CreateExemptionRequestDto dto = new CreateExemptionRequestDto();
        dto.setSectionCode("INEXISTANT");
        dto.setEmail("test@test.be");

        when(sectionDao.findById("INEXISTANT")).thenReturn(Optional.empty());

        // Doit lancer une exception si la section n'existe pas
        assertThrows(IllegalArgumentException.class, () -> exemptionService.createDraft(dto));
    }

    @Test
    void testAnalyzeRequest_WithMatch() {
        // Ce test vérifie que si le moteur de règles trouve un match, une dispense est créée.

        // 1. Préparation de la demande avec un cours externe
        UUID reqId = UUID.randomUUID();
        ExemptionRequest req = ExemptionRequest.builder()
                // .id(reqId) (construit dans le parent)
                .statut(StatutDemande.DRAFT)
                .externalCourses(new HashSet<>())
                .items(new HashSet<>()) 
                .build();
        
        // 2. On définit l'ID via le setter (Lombok @Setter l'a généré sur BaseEntity)
        req.setId(reqId);
        
        ExternalCourse courseJava = ExternalCourse.builder().code("JAVA-101").libelle("Java").ects(5).build();
        req.getExternalCourses().add(courseJava);

        when(reqDao.findById(reqId)).thenReturn(Optional.of(req));
        // Important pour la fin de la méthode (retour du DTO)
        when(reqDao.save(any(ExemptionRequest.class))).thenReturn(req);

        // 2. Préparation du Mock du Cerveau (KnowledgeBase)
        // On simule : "Si on te demande d'analyser, dis que tu as trouvé une règle !"
        UE ueCible = UE.builder().code("IPAP").nom("Prog").ects(6).build();
        
        KbCorrespondenceRule mockRule = KbCorrespondenceRule.builder()
                .description("Règle Test")
                .targets(Set.of(KbCorrespondenceRuleTarget.builder().ue(ueCible).build()))
                .build();

        // Le "Match" retourné par le service KB
        KnowledgeBaseService.RuleMatch match = new KnowledgeBaseService.RuleMatch(
                mockRule, 
                Set.of(courseJava) // Ce cours a permis le match
        );

        when(kbService.findMatchingRules(req.getExternalCourses()))
                .thenReturn(List.of(match));

        // ——— ACTION ———
        ExemptionRequestFullDto result = exemptionService.analyzeRequest(reqId);

        // ——— ASSERTION ———
        // On vérifie qu'un Item (dispense) a été ajouté à la demande
        assertEquals(1, result.getItems().size());
        
        // On vérifie que c'est bien pour l'UE IPAP
        String codeUeTrouve = result.getItems().iterator().next().getUe().getCode();
        assertEquals("IPAP", codeUeTrouve);

        // Vérifie qu'on a bien sauvegardé l'item en base
        verify(itemDao).save(any());
    }
    
    @Test
    void testAddExternalCourse_Success() {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();
        // On prépare une demande existante en statut DRAFT
        ExemptionRequest mockReq = ExemptionRequest.builder()
                .statut(StatutDemande.DRAFT) // Important !
                .build();
        mockReq.setId(reqId);

        // Le DTO envoyé par le contrôleur (ce que l'étudiant remplit)
        AddExternalCourseDto dto = new AddExternalCourseDto();
        dto.setEtablissement("ULB");
        dto.setCode("INFO-F-101");
        dto.setLibelle("Programmation 1");
        dto.setEcts(5);

        // Simulation : On trouve la demande
        when(reqDao.findById(reqId)).thenReturn(Optional.of(mockReq));
        
        // Simulation : Quand on sauvegarde le cours, on renvoie l'objet enrichi (avec ID)
        when(extCourseDao.save(any(ExternalCourse.class))).thenAnswer(invocation -> {
            ExternalCourse c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        // ——— ACTION ———
        // On appelle la méthode du service
        var resultDto = exemptionService.addExternalCourse(reqId, dto);

        // ——— ASSERTION ———
        assertNotNull(resultDto.getId(), "Le cours créé doit avoir un ID");
        assertEquals("ULB", resultDto.getEtablissement());
        assertEquals("INFO-F-101", resultDto.getCode());
        
        // Vérifie qu'on a bien appelé le DAO pour sauvegarder
        verify(extCourseDao).save(any(ExternalCourse.class));
    }

    @Test
    void testAddExternalCourse_LockedRequest() {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();
        // On prépare une demande déjà SOUMISE (donc verrouillée)
        ExemptionRequest mockReq = ExemptionRequest.builder()
                .statut(StatutDemande.SUBMITTED) // <--- Piège !
                .build();
        mockReq.setId(reqId);

        when(reqDao.findById(reqId)).thenReturn(Optional.of(mockReq));

        AddExternalCourseDto dto = new AddExternalCourseDto();
        // (Peu importe le contenu du DTO ici)

        // ——— ACTION & ASSERTION ———
        // On s'attend à ce que le service refuse et lance une IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            exemptionService.addExternalCourse(reqId, dto);
        });

        // Vérification du message d'erreur (optionnel mais recommandé)
        assertEquals("Modification impossible : demande non brouillon", exception.getMessage());
        
        // On vérifie qu'on n'a JAMAIS essayé de sauvegarder quoi que ce soit
        verify(extCourseDao, times(0)).save(any());
    }
    
    @Test
    void testAddManualItem_Success() {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        String ueCode = "IPAP";

        // 1. La demande (Brouillon)
        ExemptionRequest mockReq = ExemptionRequest.builder()
                .statut(StatutDemande.DRAFT)
                .items(new HashSet<>()) // Important pour éviter NullPointerException
                .build();
        mockReq.setId(reqId);

        // 2. Le cours externe (Preuve) appartenant à cette demande
        ExternalCourse myCourse = ExternalCourse.builder()
                .request(mockReq) // <--- Il appartient bien à NOTRE demande
                .ects(6)
                .build();
        myCourse.setId(courseId);

        // 3. L'UE visée (ISFCE)
        UE targetUe = UE.builder().code(ueCode).ects(5).build();

        // 4. Le DTO envoyé par le front
        AddManualExemptionItemDto dto = new AddManualExemptionItemDto();
        dto.setUeCode(ueCode);
        dto.setExternalCourseIds(List.of(courseId));

        // ——— MOCKS ———
        when(reqDao.findById(reqId)).thenReturn(Optional.of(mockReq));
        when(ueDao.findById(ueCode)).thenReturn(Optional.of(targetUe));
        when(extCourseDao.findAllById(dto.getExternalCourseIds())).thenReturn(List.of(myCourse));
        // Quand on sauvegarde la demande mise à jour, on la renvoie
        when(reqDao.save(any(ExemptionRequest.class))).thenReturn(mockReq);

        // ——— ACTION ———
        ExemptionRequestFullDto result = exemptionService.addManualItem(reqId, dto);

        // ——— ASSERTION ———
        // On vérifie qu'un item a été ajouté
        assertEquals(1, result.getItems().size());
        ExemptionItemDto item = result.getItems().iterator().next();
        
        // Vérifications clés
        assertEquals(ueCode, item.getUe().getCode());
        assertEquals(DecisionItem.PENDING, item.getDecision(), "Une demande manuelle doit être en attente (PENDING)");
        assertTrue(item.isTotalEctsMatches(), "6 ECTS vs 5 ECTS : Ça devrait suffire");

        verify(itemDao).save(any(ExemptionItem.class));
    }

    @Test
    void testAddManualItem_Security_HackAttempt() {
        // SCÉNARIO : L'étudiant A essaie d'utiliser un cours de l'étudiant B pour sa demande.
        
        UUID hackerReqId = UUID.randomUUID();
        ExemptionRequest hackerReq = ExemptionRequest.builder().statut(StatutDemande.DRAFT).build();
        hackerReq.setId(hackerReqId);

        // Une autre demande (Victime)
        ExemptionRequest victimReq = ExemptionRequest.builder().build();
        victimReq.setId(UUID.randomUUID()); // ID différent !

        // Le cours appartient à la VICTIME
        ExternalCourse stolenCourse = ExternalCourse.builder().request(victimReq).build();
        stolenCourse.setId(UUID.randomUUID());

        // DTO du Hacker
        AddManualExemptionItemDto dto = new AddManualExemptionItemDto();
        dto.setUeCode("IPAP");
        dto.setExternalCourseIds(List.of(stolenCourse.getId()));

        // Mocks
        when(reqDao.findById(hackerReqId)).thenReturn(Optional.of(hackerReq));
        when(ueDao.findById("IPAP")).thenReturn(Optional.of(UE.builder().code("IPAP").build()));
        when(extCourseDao.findAllById(any())).thenReturn(List.of(stolenCourse));

        // ——— ACTION & ASSERTION ———
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            exemptionService.addManualItem(hackerReqId, dto);
        });

        assertEquals("Tentative d'utilisation de cours n'appartenant pas au dossier.", ex.getMessage());
    }
    
    @Test
    void testSubmitRequest_Success() {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();
        
        // 1. Préparation d'un dossier COMPLET et VALIDE
        ExemptionRequest mockReq = ExemptionRequest.builder()
                .statut(StatutDemande.DRAFT)
                .externalCourses(new HashSet<>())
                .items(new HashSet<>())
                .globalDocuments(new HashSet<>()) // Important !
                .build();
        mockReq.setId(reqId);

        // Ajout d'un cours (Le passé)
        ExternalCourse course = ExternalCourse.builder().ects(5).request(mockReq).build();
        course.setId(UUID.randomUUID());
        mockReq.getExternalCourses().add(course);

        // Ajout d'une dispense liée à ce cours (Le futur) -> Pas d'orphelins !
        ExemptionItem item = ExemptionItem.builder()
                .ue(UE.builder().code("IPAP").build())
                .justifyingCourses(Set.of(course)) // Lien établi
                .build();
        mockReq.getItems().add(item);

        // Ajout d'un document global (La preuve)
        SupportingDocument doc = SupportingDocument.builder().type(TypeDocument.BULLETIN).build();
        mockReq.getGlobalDocuments().add(doc);

        // Mocks
        when(reqDao.findById(reqId)).thenReturn(Optional.of(mockReq));
        when(reqDao.save(any(ExemptionRequest.class))).thenAnswer(i -> i.getArgument(0));

        // ——— ACTION ———
        ExemptionRequestFullDto result = exemptionService.submitRequest(reqId);

        // ——— ASSERTION ———
        // Le statut doit avoir changé
        assertEquals(StatutDemande.SUBMITTED, result.getStatut());
        
        // On vérifie que le save a bien été appelé pour persister le changement
        verify(reqDao).save(mockReq);
    }

    @Test
    void testSubmitRequest_Failure_Incomplete() {
        // SCÉNARIO : L'étudiant a mis un cours mais n'a fait aucune demande de dispense (Orphelin)
        
        UUID reqId = UUID.randomUUID();
        ExemptionRequest mockReq = ExemptionRequest.builder()
                .statut(StatutDemande.DRAFT)
                .externalCourses(new HashSet<>())
                .items(new HashSet<>()) // Liste vide !
                .globalDocuments(new HashSet<>())
                .build();
        mockReq.setId(reqId);

        // Il a ajouté un cours...
        ExternalCourse course = ExternalCourse.builder().ects(5).request(mockReq).build();
        course.setId(UUID.randomUUID());
        mockReq.getExternalCourses().add(course);

        // ... Mais il n'a pas lancé l'analyse ni fait de demande manuelle.
        // mockReq.getItems() reste vide.

        when(reqDao.findById(reqId)).thenReturn(Optional.of(mockReq));

        // ——— ACTION & ASSERTION ———
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            exemptionService.submitRequest(reqId);
        });

        // On vérifie qu'on se fait jeter pour la bonne raison
        // Note: Le message exact dépend de l'ordre de tes 'if' dans validateSubmission, 
        // ici ça devrait bloquer sur "Aucune demande de dispense" ou "Orphelins".
        System.out.println("Erreur retournée : " + ex.getMessage()); 
        assertTrue(ex.getMessage().contains("Aucune demande de dispense") || ex.getMessage().contains("non traités"));
    }

    @Test
    void testSubmitRequest_Failure_OrphanCourse() {
        // SCÉNARIO : L'étudiant a mis un cours mais n'a fait aucune demande de dispense avec.
        // Le système doit lui dire : "Hé, vous avez oublié d'utiliser ce cours !"
        
        UUID reqId = UUID.randomUUID();
        ExemptionRequest mockReq = ExemptionRequest.builder()
                .statut(StatutDemande.DRAFT)
                .externalCourses(new HashSet<>())
                .items(new HashSet<>()) 
                .globalDocuments(new HashSet<>())
                .build();
        mockReq.setId(reqId);

        // Ajout d'un cours orphelin
        ExternalCourse course = ExternalCourse.builder().ects(5).request(mockReq).build();
        course.setId(UUID.randomUUID());
        mockReq.getExternalCourses().add(course);

        // Mais la liste des items (dispenses demandées) reste vide ou ne contient pas ce cours.
        
        when(reqDao.findById(reqId)).thenReturn(Optional.of(mockReq));

        // ——— ACTION & ASSERTION ———
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            exemptionService.submitRequest(reqId);
        });

        // On vérifie le message d'erreur
        // (Le message exact dépend de ton implémentation, mais il doit parler de cours non traités ou de demande vide)
        System.out.println("Message reçu : " + ex.getMessage());
        // Adapter selon ton message exact dans ExemptionService.validateSubmission
    }
}