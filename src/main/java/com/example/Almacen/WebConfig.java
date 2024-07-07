package com.example.Almacen;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
        .allowedOrigins("https://bodega-janys.azurewebsites.net/")
        .allowedMethods("GET", "POST")
        .allowedHeaders("Content-Type")
        .exposedHeaders("Authorization")
        .allowCredentials(true)
        .maxAge(3600);
    }
}
