package org.isfce.pid.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

/**
 * Service gérant le stockage physique des fichiers sur le système de fichiers local.
 * Ce service s'occupe de l'initialisation du répertoire racine, de la sauvegarde
 * des fichiers uploadés et de la génération des URL d'accès.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MessageSource messageSource;

    /**
     * Répertoire racine où les fichiers sont stockés.
     */
    private final Path rootLocation = Paths.get("uploads");

    /**
     * Initialise le service de stockage après l'injection des dépendances.
     * Tente de créer le répertoire "uploads" s'il n'existe pas.
     *
     * @throws RuntimeException Si le répertoire ne peut pas être créé.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(msg("err.storage.init"), e);
        }
    }

    /**
     * Sauvegarde un fichier reçu sur le disque.
     *
     * @param file Le fichier multipart à stocker.
     * @return L'URL complète (hardcodée local) permettant d'accéder au fichier stocké.
     * @throws RuntimeException Si le fichier est vide ou si une erreur d'écriture survient.
     */
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException(msg("err.storage.empty"));
        }

        try {
            // Génération d'un nom unique pour éviter les collisions
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            // Note : En production, l'URL de base devrait être configurée via @Value
            return "http://localhost:8080/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException(msg("err.storage.save"), e);
        }
    }

    /**
     * Utilitaire interne pour récupérer les messages traduits.
     */
    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}