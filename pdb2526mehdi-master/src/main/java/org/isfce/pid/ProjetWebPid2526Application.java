package org.isfce.pid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Point d'entrée principal de l'application Spring Boot.
 * Cette classe bootstrap l'application et active l'audit JPA (@EnableJpaAuditing)
 * nécessaire pour le remplissage automatique des métadonnées d'entités
 * (dates de création, de modification, etc.).
 */
@EnableJpaAuditing
@SpringBootApplication
public class ProjetWebPid2526Application {

    /**
     * Méthode principale lançant le contexte Spring Boot.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(ProjetWebPid2526Application.class, args);
    }

}