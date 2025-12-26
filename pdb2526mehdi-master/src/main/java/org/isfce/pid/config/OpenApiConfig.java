package org.isfce.pid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
       

        Info info = new Info()
                .title("API Gestion des Dispenses ISFCE")
                .version("1.0")
                .description("Cette API expose les endpoints pour la gestion des demandes de dispenses (Flow Étudiant).");

        return new OpenAPI().info(info);
    }
}