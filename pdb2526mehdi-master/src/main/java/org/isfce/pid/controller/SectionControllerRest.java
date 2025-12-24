package org.isfce.pid.controller;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.dto.SectionDto;
import org.isfce.pid.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sections") 
@RequiredArgsConstructor
public class SectionControllerRest {

    private final SectionService sectionService;

    /**
     * Récupère la liste légère des sections (Code + Nom) pour une liste déroulante.
     */
    @GetMapping("liste")
    public ResponseEntity<List<SectionDto>> getListeSections() {
        return ResponseEntity.ok(sectionService.getListeSections());
    }
}