package org.isfce.pid.controller;

import java.util.List;

import org.isfce.pid.dto.UEDto;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.service.UEService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur REST gérant les Unités d'Enseignement (UE).
 * Point d'entrée pour la consultation, la création et la suppression des UEs.
 * Les règles métier et la gestion des erreurs (404, doublons) sont déléguées au {@link UEService}.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/ue/", produces = "application/json")
@RequiredArgsConstructor
public class UEControllerRest {

    private final UEService ueService;

    /**
     * Récupère la liste complète des Unités d'Enseignement.
     *
     * @return Une liste de DTOs légers (UEDto) représentant toutes les UEs.
     */
    @GetMapping("liste")
    public ResponseEntity<List<UEDto>> getListe() {
        return ResponseEntity.ok(ueService.getListeUE());
    }

    /**
     * Récupère le détail complet d'une UE spécifique via son code.
     *
     * @param code Le code unique de l'UE (ex: "IPAP").
     * @return Le DTO complet (UEFullDto).
     */
    @GetMapping("/detail/{code}")
    public ResponseEntity<UEFullDto> getUE(@PathVariable(name = "code") String code) {
        return ResponseEntity.ok(ueService.getUE(code));
    }

    /**
     * Ajoute une nouvelle Unité d'Enseignement.
     *
     * @param ue Le DTO complet de l'UE à créer, validé par @Valid.
     * @return L'UE créée avec le statut 201 CREATED.
     */
    @PostMapping(path = "add", consumes = "application/json")
    public ResponseEntity<UEFullDto> addUEPost(@Valid @RequestBody UEFullDto ue) {
        ue = ueService.addUE(ue);
        log.debug("Ajout d'une UE: " + ue);
        return new ResponseEntity<>(ue, HttpStatus.CREATED);
    }

    /**
     * Supprime une UE existante.
     *
     * @param code Le code de l'UE à supprimer.
     * @return Le code de l'UE supprimée avec statut 200 OK.
     */
    @DeleteMapping(path = "{code}/delete")
    public ResponseEntity<String> deleteUE(@PathVariable(value = "code") String code) {
        ueService.deleteUE(code);
        return ResponseEntity.ok(code);
    }
}