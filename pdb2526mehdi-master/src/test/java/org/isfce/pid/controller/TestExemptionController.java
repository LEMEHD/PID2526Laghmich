package org.isfce.pid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.isfce.pid.dto.AddExternalCourseDto;
import org.isfce.pid.dto.CreateExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestDto;
import org.isfce.pid.dto.ExemptionRequestFullDto;
import org.isfce.pid.dto.ExternalCourseDto;
import org.isfce.pid.model.StatutDemande;
import org.isfce.pid.service.ExemptionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc // Permet de simuler des requêtes HTTP sans lancer un vrai serveur
@ActiveProfiles("testU")
public class TestExemptionController {

    @Autowired
    private MockMvc mockMvc; // L'outil pour faire les requêtes

    @Autowired
    private ObjectMapper objectMapper; // Pour transformer nos objets Java en JSON

    @MockBean
    private ExemptionService exemptionService; // On mocke le service (on ne teste pas la logique ici)

    @Test
    void testCreateDraft_Success() throws Exception {
        // ——— ARRANGEMENT ———
        CreateExemptionRequestDto createDto = new CreateExemptionRequestDto();
        createDto.setEmail("etudiant@isfce.be");
        createDto.setSectionCode("INFO");

        // Réponse simulée du service
        ExemptionRequestDto responseDto = new ExemptionRequestDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setStatut(StatutDemande.DRAFT);
        responseDto.setSectionCode("INFO");

        when(exemptionService.createDraft(any(CreateExemptionRequestDto.class))).thenReturn(responseDto);

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(post("/api/exemptions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))) // On envoie le JSON
                
                .andExpect(status().isOk()) // On veut un code 200
                .andExpect(jsonPath("$.id").exists()) // L'ID doit être présent
                .andExpect(jsonPath("$.statut").value("DRAFT")) // Le statut doit être DRAFT
                .andExpect(jsonPath("$.sectionCode").value("INFO")); // La section doit être INFO
    }

    @Test
    void testCreateDraft_ValidationFailure() throws Exception {
        // ——— ARRANGEMENT ———
        CreateExemptionRequestDto invalidDto = new CreateExemptionRequestDto();
        // Email manquant, section manquante -> Doit échouer grâce à @Valid

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(post("/api/exemptions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                
                .andExpect(status().isBadRequest()); // Doit retourner 400 Bad Request
    }

    @Test
    void testAddExternalCourse_Success() throws Exception {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();
        
        AddExternalCourseDto courseDto = new AddExternalCourseDto();
        courseDto.setEtablissement("ULB");
        courseDto.setCode("JAVA");
        courseDto.setLibelle("Java Avancé");
        courseDto.setEcts(5);

        ExternalCourseDto responseDto = ExternalCourseDto.builder()
                .id(UUID.randomUUID())
                .etablissement("ULB")
                .code("JAVA")
                .build();

        when(exemptionService.addExternalCourse(eq(reqId), any(AddExternalCourseDto.class)))
                .thenReturn(responseDto);

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(post("/api/exemptions/{reqId}/add-course", reqId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etablissement").value("ULB"))
                .andExpect(jsonPath("$.code").value("JAVA"));
    }

    @Test
    void testSubmitRequest_Success() throws Exception {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();

        ExemptionRequestFullDto fullDto = new ExemptionRequestFullDto();
        fullDto.setId(reqId);
        fullDto.setStatut(StatutDemande.SUBMITTED); // Le service renverra SUBMITTED

        when(exemptionService.submitRequest(reqId)).thenReturn(fullDto);

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(post("/api/exemptions/{reqId}/submit", reqId)
                .contentType(MediaType.APPLICATION_JSON)) // Pas de body nécessaire pour submit
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("SUBMITTED"));
        
        verify(exemptionService).submitRequest(reqId); // On vérifie que le service a été appelé
    }
    
    @Test
    void testSubmitRequest_BusinessError() throws Exception {
        // ——— ARRANGEMENT ———
        UUID reqId = UUID.randomUUID();

        // On simule une erreur métier (ex: dossier vide)
        // Le service lance une exception, le contrôleur doit la transformer en 400 Bad Request
        when(exemptionService.submitRequest(reqId))
                .thenThrow(new IllegalStateException("Dossier vide"));

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(post("/api/exemptions/{reqId}/submit", reqId))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$").value("Dossier vide")); // Le message d'erreur
    }
}