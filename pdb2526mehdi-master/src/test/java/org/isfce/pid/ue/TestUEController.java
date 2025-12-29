package org.isfce.pid.ue;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException; // Important !

import org.hamcrest.Matchers;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.mapper.UEMapper;
import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.Acquis.IdAcquis;
import org.isfce.pid.model.UE;
import org.isfce.pid.service.UEService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "testU")
public class TestUEController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UEMapper mapper;

    @MockBean
    private UEService ueServiceMock;

    static UE ipid;

    @BeforeEach
    void setUp() {
        // 1. Préparation des données
        ipid = creeIPID();
        UEFullDto ipidDto = mapper.toUEFullDto(ipid);

        // 2. Simulation du cas "SUCCÈS"
        // CORRECTION ICI : On renvoie l'objet direct, plus d'Optional
        when(ueServiceMock.getUE("IPID")).thenReturn(ipidDto);
        
        // 3. Simulation du cas "NON TROUVÉ"
        // Le service est censé lancer une exception si on cherche "TEST"
        when(ueServiceMock.getUE("TEST")).thenThrow(new NoSuchElementException("Pas trouvé"));

        // Simulation de la liste
        when(ueServiceMock.getListeUE()).thenReturn(List.of(mapper.toUELazyDto(ipid)));
    }

    @Test
    void testGetIPID() throws Exception {
        mockMvc.perform(get("/api/ue/detail/IPID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ipid.getCode()))
                .andExpect(jsonPath("ects").value(ipid.getEcts()))
                .andExpect(jsonPath("acquis").isArray());

        // On vérifie que le service a bien été appelé
        verify(ueServiceMock).getUE("IPID");
        
        // REMARQUE : J'ai supprimé 'verify(existUE)' car le contrôleur ne l'appelle plus.
        // C'est maintenant le service qui gère l'existence en interne.

        // Test du cas d'erreur (déclenché par le .thenThrow configuré dans le setUp)
        mockMvc.perform(get("/api/ue/detail/TEST")).andExpect(status().isNotFound());
    }

    @Test
    void testGetListUE() throws Exception {
        mockMvc.perform(get("/api/ue/liste"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].code").value("IPID"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].acquis").doesNotExist());
    }

    @Test
    void testPostIPID() throws Exception {
        String jsonITest = """
                {
                  "code": "ITEST",
                  "ref": "TTT",
                  "nom": "Cours Test",
                  "nbPeriodes": 20,
                  "ects": 4,
                  "prgm": "Contenu",
                  "acquis": [
                    { "num": 1, "acquis": "Aq1", "pourcentage": 60 },
                    { "num": 2, "acquis": "Aq2", "pourcentage": 40 }
                  ]
                }
                """;

        // On utilise 'any()' pour dire "peu importe l'objet envoyé, si c'est un DTO, retourne ça"
        // C'est plus robuste pour les tests
        UEFullDto expectedReturn = new UEFullDto();
        expectedReturn.setCode("ITEST");
        // ... (on pourrait remplir le reste si besoin pour le test)

        // Simulation : L'ajout fonctionne et renvoie l'objet (plus besoin de simuler existUE)
        when(ueServiceMock.addUE(any(UEFullDto.class))).thenReturn(expectedReturn);

        mockMvc.perform(post("/api/ue/add")
                .contentType("application/json")
                .content(jsonITest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ITEST"));
    }

    @Test
    void testPostBadUE() throws Exception {
        String jsonBad = """
                {
                  "code": "ITEST",
                  "ref": "TTT",
                  "nom": "Cours Test",
                  "nbPeriodes": 0,
                  "ects": 0,
                  "acquis": [
                    { "acquis": "", "pourcentage": 0 },
                    { "num": 2, "acquis": "Aq2", "pourcentage": 40 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/ue/add")
                .contentType("application/json")
                .content(jsonBad)
                .header("Accept-Language", "fr"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nbPeriodes", containsString("Il faut minimum")))
                .andExpect(jsonPath("$.ects", containsString("Il faut minimum")));
    }

    private static UE creeIPID() {
        String code = "7534 35 U32 D2";
        Acquis[] acquis = {
                new Acquis(new IdAcquis("IPID", 1), "de produire...", 50),
                new Acquis(new IdAcquis("IPID", 2), "d’implémenter...", 30),
                new Acquis(new IdAcquis("IPID", 3), "de déployer...", 20)
        };
        String prgm = "* Contenu du programme...";
        List<Acquis> liste = new ArrayList<>(Arrays.asList(acquis));
        return UE.builder().code("IPID").ects(9).nbPeriodes(100).nom("PROJET").prgm(prgm).ref(code).acquis(liste).build();
    }
}