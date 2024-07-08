package com.example.Almacen;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:9095", 
                        "https://bodega-janys.azurewebsites.net",
                        "https://www.google.com", 
                        "https://www.gstatic.com",
                        "https://cdn.jsdelivr.net",
                        "https://cdnjs.cloudflare.com",
                        "https://code.jquery.com",
                        "https://kit.fontawesome.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
