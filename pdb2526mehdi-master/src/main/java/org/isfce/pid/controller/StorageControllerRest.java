package org.isfce.pid.controller;

import lombok.RequiredArgsConstructor;
import org.isfce.pid.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageControllerRest {

    private final FileStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // 1. On sauvegarde le fichier
        String url = storageService.store(file);
        
        // 2. On renvoie l'URL au frontend
        return ResponseEntity.ok(Map.of("url", url));
    }
}