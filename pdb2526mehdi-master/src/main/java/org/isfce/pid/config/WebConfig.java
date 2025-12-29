package org.isfce.pid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration globale de la couche Web (Spring MVC).
 * Cette classe définit les règles de sécurité transversales (CORS)
 * et la manière dont les fichiers statiques (uploads) sont servis.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure les règles CORS (Cross-Origin Resource Sharing).
     * Autorise explicitement l'application Frontend (Vite) à consommer cette API,
     * en permettant l'envoi de cookies/credentials.
     *
     * @param registry Le registre de configuration CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * Configure l'exposition des fichiers statiques.
     * Mappe l'URL "/uploads/**" vers le dossier physique "uploads/" à la racine du serveur.
     *
     * @param registry Le registre des gestionnaires de ressources.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}