package tn.esprit.backendDevops.config; // Adapte le package si besoin

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// @EnableWebMvc // Décommente seulement si tu as des problèmes sans, peut interférer avec la config auto de Spring Boot
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Autorise CORS pour toutes les routes sous /api/
                .allowedOrigins("http://localhost:4200") // Autorise spécifiquement ton frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                .allowedHeaders("*") // En-têtes autorisés
                .allowCredentials(false); // Mettre à true si tu gères des cookies/sessions authentifiées
    }

    // Alternative plus simple (souvent suffisante pour le développement) :
    /*
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Chemin de ton API
                        .allowedOrigins("http://localhost:4200") // Origine de ton frontend React
                        .allowedMethods("*"); // Autorise toutes les méthodes pour la simplicité en dev
                       // .allowedHeaders("*") // Par défaut, autorise les en-têtes courants
                       // .allowCredentials(false);
            }
        };
    }
    */
}