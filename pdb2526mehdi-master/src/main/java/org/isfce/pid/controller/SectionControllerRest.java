package org.isfce.pid.controller;

import java.util.List;

import org.isfce.pid.dto.SectionDto;
import org.isfce.pid.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST gérant les sections d'études (ex: Informatique, Comptabilité).
 * Fournit les endpoints de consultation des référentiels de sections.
 */
@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionControllerRest {

    private final SectionService sectionService;

    /**
     * Récupère la liste simplifiée de toutes les sections.
     * Utile pour alimenter les listes déroulantes (Code + Nom).
     *
     * @return Une liste de DTOs légers représentant les sections disponibles.
     */
    @GetMapping("liste")
    public ResponseEntity<List<SectionDto>> getListeSections() {
        return ResponseEntity.ok(sectionService.getListeSections());
    }
}