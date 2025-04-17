package com.esprit.ms.pidevbackend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailVerificationService {

    private static final String API_URL = "https://api.hunter.io/v2/email-verifier?email={email}&api_key={api_key}";
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "6714e9124d0dccb3b398f91d7bf07bcb9a71acbe";


    public boolean verifyEmail(String email) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("api_key", apiKey);

        try {
            Map response = restTemplate.getForObject(API_URL, Map.class, params);
            System.out.println("Réponse Hunter.io: " + response);

            if (response == null || !response.containsKey("data")) {
                return false;
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String status = (String) data.get("status");

            return "valid".equalsIgnoreCase(status) || "accept_all".equalsIgnoreCase(status);
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'email : " + e.getMessage());
            return false;
        }
    }

}
