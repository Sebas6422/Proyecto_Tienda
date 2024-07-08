package com.example.Almacen;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9095", "https://bodega-janys.azurewebsites.net")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*") 
                .allowCredentials(true); 
    }
}
