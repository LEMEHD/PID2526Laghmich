package org.isfce.pid.ue;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;

import org.isfce.pid.dto.AcquisFullDto;
import org.isfce.pid.dto.UEDto;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.service.UEService;
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
public class TestUEController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UEService ueService;

    // ——— TESTS DE LECTURE (GET) ———

    @Test
    @DisplayName("GET /api/ue/detail/{code} - Succès")
    void testGetUE_Success() throws Exception {
        // CORRECTION : Utilisation du Builder pour être sûr
        UEFullDto dto = UEFullDto.builder()
                .code("IPID")
                .nom("Projet Intégré")
                .ects(6)
                .build();

        when(ueService.getUE("IPID")).thenReturn(dto);

        mockMvc.perform(get("/api/ue/detail/IPID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("IPID"))
                .andExpect(jsonPath("$.ects").value(6));
    }

    @Test
    @DisplayName("GET /api/ue/detail/{code} - Non Trouvé")
    void testGetUE_NotFound() throws Exception {
        // Le service lance NoSuchElementException -> 404 (Via AdviceController)
        when(ueService.getUE("INCONNU")).thenThrow(new NoSuchElementException("UE introuvable"));

        mockMvc.perform(get("/api/ue/detail/INCONNU"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/ue/liste - Liste complète")
    void testGetListeUE() throws Exception {
        // CORRECTION MAJEURE ICI : Utilisation du Builder au lieu de new UEDto(arg1, arg2)
        UEDto u1 = UEDto.builder().code("IPID").nom("Projet").build();
        UEDto u2 = UEDto.builder().code("JAVA").nom("Java").build();

        when(ueService.getListeUE()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/ue/liste"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("IPID"));
    }

    // ——— TESTS D'ÉCRITURE (POST / DELETE) ———

    @Test
    @DisplayName("POST /api/ue/add - Création Succès")
    void testAddUE_Success() throws Exception {
        // 1. Préparation d'un Acquis valide
    	AcquisFullDto acquisTest = new AcquisFullDto(1, "Savoir compter", 50);

        // 2. Création de l'UE avec la liste d'acquis obligatoire
        UEFullDto newUe = UEFullDto.builder()
                .code("MATH")
                .nom("Mathématiques")
                .ref("MATH101")
                .nbPeriodes(40)
                .ects(5)
                .prgm("Contenu du cours de maths...")
                .acquis(List.of(acquisTest)) // <--- CORRECTION CRUCIALE ICI
                .build();

        // Le service retourne l'objet créé
        when(ueService.addUE(any(UEFullDto.class))).thenReturn(newUe);

        mockMvc.perform(post("/api/ue/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUe)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.code").value("MATH"))
                .andExpect(jsonPath("$.acquis").isArray()); // On peut vérifier que les acquis sont là
    }

    @Test
    @DisplayName("POST /api/ue/add - Erreur Validation (ECTS invalides)")
    void testAddUE_ValidationFail() throws Exception {
        // UE invalide (ECTS négatif, nom vide)
        // Ici on envoie une String JSON brute pour être sûr de tester la validation @Valid du Controller
        String jsonBad = """
                {
                  "code": "BAD",
                  "nom": "",
                  "nbPeriodes": -10,
                  "ects": 0
                }
                """;

        mockMvc.perform(post("/api/ue/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBad)
                .header("Accept-Language", "fr")) // Pour avoir les messages en français
                .andExpect(status().isBadRequest()) // 400
                // Vérifie qu'on a bien des erreurs sur les champs spécifiques
                .andExpect(jsonPath("$.nbPeriodes").exists())
                .andExpect(jsonPath("$.ects").exists());
    }

    @Test
    @DisplayName("DELETE /api/ue/{code}/delete - Suppression Succès")
    void testDeleteUE_Success() throws Exception {
        String code = "OLD";

        mockMvc.perform(delete("/api/ue/{code}/delete", code))
                .andExpect(status().isOk())
                .andExpect(content().string(code)); // Le contrôleur renvoie le code supprimé

        verify(ueService).deleteUE(code);
    }
    
    @Test
    @DisplayName("DELETE /api/ue/{code}/delete - Non Trouvé")
    void testDeleteUE_NotFound() throws Exception {
        String code = "INCONNU";
        
        doThrow(new NoSuchElementException("Pas trouvé")).when(ueService).deleteUE(code);

        mockMvc.perform(delete("/api/ue/{code}/delete", code))
                .andExpect(status().isNotFound());
    }
}