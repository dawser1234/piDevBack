/*package com.esprit.ms.pidevbackend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig  {
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/pidev/**")
                //registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        //.allowedHeaders("*")// Permet tous les en-têtes
                        .allowedHeaders("Content-Type", "Authorization","Accept")
                        .allowCredentials(true);

            }
        };
    }
}*/
package com.esprit.ms.pidevbackend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/pidev/**")
                //registry.addMapping("/**") // Appliquer CORS sur toutes les routes
                        .allowedOrigins("http://localhost:4200") // Autoriser Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Autoriser toutes les méthodes
                        .allowedHeaders("Content-Type", "Authorization", "Accept") // Autoriser les headers
                        .allowCredentials(true);
            }
        };
    }

}

