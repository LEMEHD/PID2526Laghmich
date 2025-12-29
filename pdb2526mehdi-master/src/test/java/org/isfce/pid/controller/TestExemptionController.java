package org.isfce.pid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import org.isfce.pid.controller.error.DuplicateException;
import org.isfce.pid.dto.AddExternalCourseDto;
import org.isfce.pid.dto.AddManualExemptionItemDto;
import org.isfce.pid.dto.CreateExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestFullDto;
import org.isfce.pid.dto.ExternalCourseDto;
import org.isfce.pid.model.StatutDemande;
import org.isfce.pid.service.ExemptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testU")
public class TestExemptionController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExemptionService exemptionService;

    // ===================================================================================
    // 1. TESTS DE CONSULTATION (GET)
    // ===================================================================================

    @Test
    @DisplayName("GET /api/exemptions/{id} - Succès")
    void testGetRequestDetail_Success() throws Exception {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();
        ExemptionRequestFullDto fullDto = new ExemptionRequestFullDto();
        fullDto.setId(reqId);
        fullDto.setStatut(StatutDemande.DRAFT);
        fullDto.setSectionCode("INFO");

        // Appel de la méthode réelle du service : getRequestDetail
        when(exemptionService.getRequestDetail(reqId)).thenReturn(fullDto);

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(get("/api/exemptions/{reqId}", reqId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reqId.toString()))
                .andExpect(jsonPath("$.statut").value("DRAFT"))
                .andExpect(jsonPath("$.sectionCode").value("INFO"));
    }

    @Test
    @DisplayName("GET /api/exemptions/{id} - Non Trouvé (404)")
    void testGetRequestDetail_NotFound() throws Exception {
        UUID reqId = UUID.randomUUID();
        
        // Simulation : Le service ne trouve pas l'entité
        when(exemptionService.getRequestDetail(reqId))
                .thenThrow(new EntityNotFoundException("Demande introuvable"));

        // MonAdviceRestController transforme EntityNotFoundException en 404
        mockMvc.perform(get("/api/exemptions/{reqId}", reqId))
                .andExpect(status().isNotFound());
    }

    // ===================================================================================
    // 2. TESTS DE CRÉATION & MODIFICATION (POST)
    // ===================================================================================

    @Test
    @DisplayName("POST /create - Création d'un brouillon")
    void testCreateDraft_Success() throws Exception {
        CreateExemptionRequestDto createDto = new CreateExemptionRequestDto();
        createDto.setEmail("etudiant@isfce.be");
        createDto.setSectionCode("INFO");

        ExemptionRequestDto responseDto = ExemptionRequestDto.builder()
                .id(UUID.randomUUID())
                .statut(StatutDemande.DRAFT)
                .build();

        when(exemptionService.createDraft(any(CreateExemptionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/exemptions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("DRAFT"));
    }

    @Test
    @DisplayName("POST /add-course - Ajout d'un cours externe")
    void testAddExternalCourse_Success() throws Exception {
        UUID reqId = UUID.randomUUID();
        
        AddExternalCourseDto courseDto = new AddExternalCourseDto();
        courseDto.setEtablissement("ULB");
        courseDto.setCode("JAVA");
        courseDto.setLibelle("Java OO");
        courseDto.setEcts(5);

        ExternalCourseDto responseDto = ExternalCourseDto.builder()
                .id(UUID.randomUUID())
                .code("JAVA")
                .build();

        when(exemptionService.addExternalCourse(eq(reqId), any(AddExternalCourseDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/exemptions/{reqId}/add-course", reqId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("JAVA"));
    }

    // ===================================================================================
    // 3. TESTS MÉTIER AVANCÉS (Analyse & Ajout Manuel)
    // ===================================================================================

    @Test
    @DisplayName("POST /analyze - Lancement du moteur de règles")
    void testAnalyzeRequest_Success() throws Exception {
        UUID reqId = UUID.randomUUID();
        ExemptionRequestFullDto fullDto = new ExemptionRequestFullDto();
        fullDto.setId(reqId);
        // Simulation : le moteur a généré des items (même vide pour le test JSON)
        fullDto.setItems(new HashSet<>());

        when(exemptionService.analyzeRequest(reqId)).thenReturn(fullDto);

        mockMvc.perform(post("/api/exemptions/{reqId}/analyze", reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("POST /add-manual-item - Succès")
    void testAddManualItem_Success() throws Exception {
        UUID reqId = UUID.randomUUID();
        
        AddManualExemptionItemDto manualDto = new AddManualExemptionItemDto();
        manualDto.setUeCode("IPAP");
        manualDto.setExternalCourseIds(List.of(UUID.randomUUID()));

        ExemptionRequestFullDto updatedReq = new ExemptionRequestFullDto();
        updatedReq.setId(reqId);

        when(exemptionService.addManualItem(eq(reqId), any(AddManualExemptionItemDto.class)))
                .thenReturn(updatedReq);

        mockMvc.perform(post("/api/exemptions/{reqId}/add-manual-item", reqId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manualDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /add-manual-item - Erreur Doublon (400)")
    void testAddManualItem_DuplicateError() throws Exception {
        UUID reqId = UUID.randomUUID();
        AddManualExemptionItemDto manualDto = new AddManualExemptionItemDto();
        manualDto.setUeCode("IPAP");
        manualDto.setExternalCourseIds(List.of(UUID.randomUUID()));

        // Simulation : Le service lève l'exception DuplicateException(msg, champ)
        when(exemptionService.addManualItem(eq(reqId), any()))
                .thenThrow(new DuplicateException("Dispense déjà demandée", "ueCode"));

        // MonAdviceRestController intercepte DuplicateException et renvoie :
        // status: 400 Bad Request
        // body: { "ueCode": "Dispense déjà demandée" }
        mockMvc.perform(post("/api/exemptions/{reqId}/add-manual-item", reqId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manualDto)))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.ueCode").value("Dispense déjà demandée"));
    }

    // ===================================================================================
    // 4. TESTS DE SOUMISSION & SUPPRESSION
    // ===================================================================================

    @Test
    @DisplayName("POST /submit - Soumission réussie")
    void testSubmitRequest_Success() throws Exception {
        UUID reqId = UUID.randomUUID();
        ExemptionRequestFullDto submittedDto = new ExemptionRequestFullDto();
        submittedDto.setStatut(StatutDemande.SUBMITTED);

        when(exemptionService.submitRequest(reqId)).thenReturn(submittedDto);

        mockMvc.perform(post("/api/exemptions/{reqId}/submit", reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("SUBMITTED"));
        
        verify(exemptionService).submitRequest(reqId);
    }

    @Test
    @DisplayName("POST /submit - Échec métier (Dossier vide)")
    void testSubmitRequest_BusinessError() throws Exception {
        UUID reqId = UUID.randomUUID();

        // Le service renvoie une erreur d'état (ex: orphelins, pas de docs)
        when(exemptionService.submitRequest(reqId))
                .thenThrow(new IllegalStateException("Dossier incomplet"));

        mockMvc.perform(post("/api/exemptions/{reqId}/submit", reqId))
                .andExpect(status().isBadRequest()) // 400 géré par MonAdviceRestController
                .andExpect(jsonPath("$").value("Dossier incomplet"));
    }

    @Test
    @DisplayName("DELETE /api/exemptions/{id} - Suppression brouillon")
    void testDeleteDraft_Success() throws Exception {
        UUID reqId = UUID.randomUUID();

        // La méthode est void, on ne mocke rien de spécial, juste qu'elle ne plante pas
        
        mockMvc.perform(delete("/api/exemptions/{reqId}", reqId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(exemptionService).deleteDraft(reqId);
    }
}