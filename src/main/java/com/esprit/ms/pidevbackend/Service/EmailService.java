package com.esprit.ms.pidevbackend.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Injection de dépendance pour JavaMailSender

    private Map<String, String> verificationCodes = new HashMap<>(); // Stocker les codes en mémoire

    // Méthode pour envoyer un code de vérification
    public void sendVerificationCode(String email) {
        String verificationCode = UUID.randomUUID().toString(); // Générer un code unique
        verificationCodes.put(email, verificationCode); // Stocker le code

        // Créer et envoyer l'email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Code de Vérification");
        message.setText("Votre code de vérification est : " + verificationCode);
        mailSender.send(message); // Envoyer l'email

        System.out.println("Code de vérification envoyé à " + email);
    }

    // Méthode pour vérifier le code
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    // Méthode pour effacer le code après utilisation
    public void clearCode(String email) {
        verificationCodes.remove(email); // Supprimer le code après utilisation
    }
}
