package org.isfce.pid.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.isfce.pid.dto.SectionDto;
import org.isfce.pid.service.SectionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testU")
public class TestSectionController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SectionService sectionService;

    @Test
    @DisplayName("GET /api/sections/liste - Récupérer les sections")
    void testGetListeSections() throws Exception {
        // ——— ARRANGEMENT ———
        SectionDto sec1 = new SectionDto("INFO", "Informatique");
        SectionDto sec2 = new SectionDto("COMPTA", "Comptabilité");
        
        when(sectionService.getListeSections()).thenReturn(List.of(sec1, sec2));

        // ——— ACTION & ASSERTION ———
        mockMvc.perform(get("/api/sections/liste")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("INFO"))
                .andExpect(jsonPath("$[1].code").value("COMPTA"));
    }
}