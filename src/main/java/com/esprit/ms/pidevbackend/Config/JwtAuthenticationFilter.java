package com.esprit.ms.pidevbackend.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component // Ajoutez cette annotation
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


   /*@Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
           throws ServletException, IOException {
       String path = request.getRequestURI();

       // Corriger les URLs des endpoints autorisés (sans token)
       if (path.equals(request.getContextPath() + "/api/users/login") ||
               path.equals(request.getContextPath() + "/api/users/add")||
               path.startsWith("/oauth2") ||
       path.equals(request.getContextPath() + "/login/oauth2/code/google")) {
           chain.doFilter(request, response); // Passer la requête sans vérification JWT
           return;
       }

       String token = request.getHeader("Authorization");

       if (token != null && token.startsWith("Bearer ")) {
           token = token.substring(7); // Enlever "Bearer " du token
           if (jwtTokenProvider.validateToken(token)) {
               String username = jwtTokenProvider.getUsernameFromToken(token);
               String role = jwtTokenProvider.getRoleFromToken(token); // Extraire le rôle

               // Créer l'objet d'authentification avec le rôle
               List<GrantedAuthority> authorities = new ArrayList<>();
               authorities.add(new SimpleGrantedAuthority(role)); // Ajouter le rôle aux autorités

               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(username, null, authorities);
               SecurityContextHolder.getContext().setAuthentication(authentication);
               System.out.println("Authentication successful for user: " + username);
           } else {
               response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
               return;
           }
       } else {
           response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No JWT token found");
           return;
       }

       chain.doFilter(request, response);
   }*/
   /*@Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
           throws ServletException, IOException {
       String path = request.getRequestURI();
       System.out.println("🔍 Requête interceptée : " + path);

       if (path.equals(request.getContextPath() + "/api/users/login") ||
               path.equals(request.getContextPath() + "/api/users/add") ||
               path.startsWith("/oauth2") ||
               path.equals(request.getContextPath() + "/login/oauth2/code/google")||
               path.startsWith("/api/users/approve-login")) {
           System.out.println("✅ Pas de vérification JWT pour : " + path);
           chain.doFilter(request, response);
           return;
       }

       String token = request.getHeader("Authorization");
       System.out.println("📌 Token reçu : " + token);

       if (token != null && token.startsWith("Bearer ")) {
           token = token.substring(7);
           System.out.println("🔍 Token après suppression 'Bearer ': " + token);

           if (jwtTokenProvider.validateToken(token)) {
               String username = jwtTokenProvider.getUsernameFromToken(token);
               String role = jwtTokenProvider.getRoleFromToken(token);
               System.out.println("🔑 Utilisateur authentifié : " + username + " | Rôle : " + role);

               List<GrantedAuthority> authorities = new ArrayList<>();
               authorities.add(new SimpleGrantedAuthority(role));

               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(username, null, authorities);
               SecurityContextHolder.getContext().setAuthentication(authentication);
               System.out.println("✅ Authentification enregistrée dans SecurityContext");
           } else {
               System.out.println("❌ JWT invalide !");
               response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
               return;
           }
       } else {
           System.out.println("❌ Aucun token JWT trouvé !");
           response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No JWT token found");
           return;
       }

       chain.doFilter(request, response);
   }*/
   @Override
   protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain)
           throws ServletException, IOException {
       String requestPath = httpRequest.getRequestURI();
       System.out.println("🔍 Requête interceptée : " + requestPath);

       // Liste des endpoints publics (sans vérification JWT)
       if (isPublicEndpoint(requestPath, httpRequest.getContextPath())) {
           System.out.println("✅ Pas de vérification JWT pour : " + requestPath);
           filterChain.doFilter(httpRequest, httpResponse);
           return;
       }

       // Récupérer le token JWT de l'en-tête Authorization
       String token = extractJwtToken(httpRequest);
       System.out.println("📌 Token reçu : " + token);

       if (token == null) {
           System.out.println("❌ Aucun token JWT trouvé !");
           httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No JWT token found");
           return;
       }

       // Valider et extraire les informations du token JWT
       try {
           if (jwtTokenProvider.validateToken(token)) {
               String username = jwtTokenProvider.getUsernameFromToken(token);
               String role = jwtTokenProvider.getRoleFromToken(token);
               System.out.println("🔑 Utilisateur authentifié : " + username + " | Rôle : " + role);

               // Créer l'objet d'authentification avec le rôle
               List<GrantedAuthority> authorities = new ArrayList<>();
               authorities.add(new SimpleGrantedAuthority(role));

               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(username, null, authorities);
               SecurityContextHolder.getContext().setAuthentication(authentication);
               System.out.println("✅ Authentification enregistrée dans SecurityContext");
           } else {
               System.out.println("❌ JWT invalide !");
               httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
               return;
           }
       } catch (Exception e) {
           System.out.println("❌ Erreur lors de la validation du token : " + e.getMessage());
           httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error validating token");
           return;
       }

       // Passer la requête au filtre suivant
       filterChain.doFilter(httpRequest, httpResponse);
   }

    /**
     * Vérifie si l'URL correspond à un endpoint public.
     */
    private boolean isPublicEndpoint(String requestPath, String contextPath) {
        return requestPath.equals(contextPath + "/api/users/login") ||
                requestPath.equals(contextPath + "/api/users/add") ||
                requestPath.equals(contextPath + "/api/users/add-recaptcha") ||
                requestPath.equals(contextPath + "/api/users/forgot-password") ||
                requestPath.equals(contextPath + "/update-profile/{id}") ||
                requestPath.equals(contextPath + "/api/users/reset-password") ||
                requestPath.startsWith("/oauth2") ||
                requestPath.equals(contextPath + "/login/oauth2/code/google") ||
                requestPath.startsWith("/api/users/approve-login") ||
                requestPath.equals(contextPath + "/code/google"); // Ajouter /code/google comme endpoint public
    }

    /**
     * Extrait le token JWT de l'en-tête Authorization.
     */
    private String extractJwtToken(HttpServletRequest httpRequest) {
        String bearerToken = httpRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Supprimer "Bearer " pour obtenir le token
        }
        return null;
    }



}