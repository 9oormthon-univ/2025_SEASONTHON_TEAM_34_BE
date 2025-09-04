package com.example.clearfootprint.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override public void addCorsMappings(CorsRegistry r) {
                r.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "http://localhost:8081")
                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}