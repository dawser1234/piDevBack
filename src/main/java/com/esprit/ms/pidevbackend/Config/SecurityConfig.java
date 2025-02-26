/*package com.esprit.ms.pidevbackend.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity


public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour simplifier les tests
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Permettre toutes les requêtes sans authentification
                );

        return http.build();
    }



}*/
package com.esprit.ms.pidevbackend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                //.cors() // Activer CORS
                //.and()
                // Désactiver CSRF pour simplifier les tests
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/login","/api/users/add").permitAll()
                        //.requestMatchers("/api/users/getAll").hasRole("ADMIN")// Autoriser l'accès à la route de login
                        .requestMatchers("/api/users/getAll").permitAll()

                        .anyRequest().authenticated() // Nécessite une authentification pour toutes les autres requêtes
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Ajouter le filtre JWT
                .logout(logout -> logout.permitAll());
        System.out.println("Authorization rules applied"); // Log des règles
// Autoriser la déconnexion

        return http.build();
    }
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF
                .cors(cors -> cors.configure(http)) // Activer CORS avec WebConfig
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/login", "/api/users/add").permitAll()
                        .requestMatchers("/api/users/getAll").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Ajouter le filtre JWT
                .logout(logout -> logout.permitAll());

        return http.build();
    }*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors() // Activer CORS
                .and()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/login", "/api/users/add").permitAll()
                        .requestMatchers("/api/users/getAll").permitAll()// Autorise les accès sans token
                        .anyRequest().authenticated() // Nécessite une authentification pour toutes les autres requêtes
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Ajouter le filtre JWT
                //.logout(logout -> logout.permitAll());
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout") // URL du logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.getWriter().write("{\"message\": \"Déconnexion réussie\"}");
                            response.getWriter().flush();
                        })
                        .invalidateHttpSession(true) // Invalider la session
                        .deleteCookies("JSESSIONID") // Supprimer les cookies
                );

        return http.build();
    }

}