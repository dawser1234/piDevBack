package com.esprit.ms.pidevbackend.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@Component // Ajoutez cette annotation
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


   /* @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Enlever "Bearer " du token
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication successful for user: " + username);
            } else {
                System.out.println("Invalid JWT token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return; // Ne pas continuer le filtrage
            }
        } else {
            System.out.println("No JWT token found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No JWT token found");
            return; // Ne pas continuer le filtrage
        }
        chain.doFilter(request, response);
    }*/
   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
           throws ServletException, IOException {
       String path = request.getRequestURI();

       // Corriger les URLs des endpoints autorisés (sans token)
       if (path.equals(request.getContextPath() + "/api/users/login") ||
               path.equals(request.getContextPath() + "/api/users/add")) {
           chain.doFilter(request, response); // Passer la requête sans vérification JWT
           return;
       }

       String token = request.getHeader("Authorization");

       if (token != null && token.startsWith("Bearer ")) {
           token = token.substring(7); // Enlever "Bearer " du token
           if (jwtTokenProvider.validateToken(token)) {
               String username = jwtTokenProvider.getUsernameFromToken(token);
               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
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
   }


}