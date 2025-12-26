package org.isfce.pid.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    // Dossier où seront stockés les fichiers (à la racine du projet)
    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage !", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Fichier vide");
            }
            // On génère un nom unique pour éviter les conflits (ex: a1b2-bulletin.pdf)
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // On copie le fichier dans le dossier uploads
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            
            // On retourne l'URL relative accessible par le navigateur
            return "http://localhost:8080/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier", e);
        }
    }
}