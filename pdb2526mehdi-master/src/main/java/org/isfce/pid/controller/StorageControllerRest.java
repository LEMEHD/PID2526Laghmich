package org.isfce.pid.controller;

import java.util.Map;

import org.isfce.pid.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST gérant le stockage des fichiers.
 * Expose les endpoints permettant l'upload de documents (PDF, images, etc.)
 * vers le système de fichiers du serveur.
 */
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageControllerRest {

    private final FileStorageService storageService;

    /**
     * Téléverse (upload) un fichier unique sur le serveur.
     * Le fichier est stocké physiquement et une URL d'accès est générée.
     *
     * @param file Le fichier binaire envoyé par le client (Multipart).
     * @return Une Map contenant l'URL publique d'accès au fichier (clé : "url").
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = storageService.store(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}