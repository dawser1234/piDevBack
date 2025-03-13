package com.esprit.ms.pidevbackend.Controller;
import com.esprit.ms.pidevbackend.Config.JwtTokenProvider;
import com.esprit.ms.pidevbackend.Entity.LoginAttempt;
import com.esprit.ms.pidevbackend.Entity.Presence;
import com.esprit.ms.pidevbackend.Entity.Role;
import com.esprit.ms.pidevbackend.Entity.User;
import com.esprit.ms.pidevbackend.Response.AuthResponse;
import com.esprit.ms.pidevbackend.Service.EmailService;
import com.esprit.ms.pidevbackend.Service.UserServices;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:4200")

public class UserRestController {
    private  PasswordEncoder passwordEncoder;





    private  UserServices userServices;
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;





    @PostMapping("add")
    @PermitAll
    public User addUser(@RequestBody User user) {
        return userServices.addUser(user);
    }

    @GetMapping("get/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userServices.getUserbyId(id);
    }

    @GetMapping("getAll")
    public List<User> getAllUsers() {
        return userServices.getallUser();
    }

    @DeleteMapping("delete/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userServices.deleteUser(id);
    }

    /*@PutMapping("/update/user/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userServices.UpdateUser(id, user);
    }*/@PutMapping("/update/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userServices.UpdateUser(id, user);
        if (updatedUser != null) {
            // Générer un nouveau token pour l'utilisateur mis à jour
            String token = jwtTokenProvider.generateToken(updatedUser.getEmailU(), updatedUser.getRole().name(),updatedUser.getIdU());

            // Retourner l'utilisateur et le token
            return ResponseEntity.ok(new AuthResponse(token)); // Retourner le token dans la réponse
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
    }


    @PostMapping("{userId}/presences/add")
    public Presence addPresence(@PathVariable Long userId, @RequestBody Presence presence) {
        User user = new User(userId); // Utiliser le constructeur avec l'ID
        presence.setUser(user); // Associer la présence à l'utilisateur
        return userServices.addPresence(presence);
    }

    @PutMapping("/update/presences/{idP}")
    public Presence updatePresence(@PathVariable Long idP, @RequestBody Presence presence) {
        return userServices.UpdatePresence(idP, presence);
    }

    @DeleteMapping("presences/delete/{id}")
    public void deletePresence(@PathVariable("id") Long id) {
        userServices.deletepresence(id);
    }

    @GetMapping("presences/get/{id}")
    public Presence getPresenceById(@PathVariable("id") Long id) {
        return userServices.getPresenceByid(id);
    }
    @GetMapping("{userId}/presences")
    public List<Presence> getPresencesByUserId(@PathVariable Long userId) {
        return userServices.getPresencesByid(userId);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        AuthResponse authResponse = userServices.login(user);
        if (authResponse != null) {
            return ResponseEntity.ok(authResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

   /*
    @GetMapping("/code/google")
    public ResponseEntity<?> googleLoginCallback(@AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        Map<String, Object> userAttributes = authentication.getPrincipal().getAttributes();

        String email = (String) userAttributes.get("email");
        String name = (String) userAttributes.get("name");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found in user attributes");
        }

        // Vérifiez si l'utilisateur existe déjà
        User existingUser = userServices.getUserByemail(email);
        if (existingUser == null) {
            // Créer un nouvel utilisateur
            User newUser = new User();
            newUser.setEmailU(email);
            newUser.setNomU(name);
            userServices.addUser(newUser);
        }

        String token = jwtTokenProvider.generateToken(email, "USER"); // ou autre rôle
        return ResponseEntity.ok(new AuthResponse(token, userAttributes));
    }*/
   @PostMapping("/code/google")
   public ResponseEntity<?> googleLoginCallback(@RequestBody Map<String, String> body) {
       String code = body.get("code");
       if (code == null) {
           return ResponseEntity.badRequest().body("Code not found");
       }

       // 1️⃣ Échanger le code contre un token Google
       RestTemplate restTemplate = new RestTemplate();
       String googleTokenUrl = "https://oauth2.googleapis.com/token";

       Map<String, String> params = new HashMap<>();
       params.put("code", code);
       params.put("client_id", "994519531998-tsrffi97f9bt8jmvraeffcjodd09kt6h.apps.googleusercontent.com");
       params.put("client_secret", "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"); // Remplace par ton secret client
       params.put("redirect_uri", "http://localhost:4200/auth/callback");
       params.put("grant_type", "authorization_code");

       ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUrl, params, Map.class);
       if (!response.getStatusCode().is2xxSuccessful()) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid code");
       }

       String accessToken = (String) response.getBody().get("access_token");

       // 2️⃣ Récupérer les infos de l'utilisateur depuis Google
       HttpHeaders headers = new HttpHeaders();
       headers.set("Authorization", "Bearer " + accessToken);
       HttpEntity<String> entity = new HttpEntity<>(headers);

       ResponseEntity<Map> userResponse = restTemplate.exchange(
               "https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, entity, Map.class
       );

       Map<String, Object> userAttributes = userResponse.getBody();
       String email = (String) userAttributes.get("email");
       String name = (String) userAttributes.get("name");

       if (email == null) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
       }

       // 3️⃣ Vérifier si l'utilisateur existe dans la base de données
       User existingUser = userServices.getUserByemail(email);
       if (existingUser == null) {
           existingUser = new User();
           existingUser.setEmailU(email);
           existingUser.setNomU(name);
           existingUser = userServices.addUser(existingUser);
       }

       // 4️⃣ Générer un JWT avec l'ID de l'utilisateur
       String token;
       try {
           token = jwtTokenProvider.generateToken(email, "USER",existingUser.getIdU()); // Ajout de l'ID utilisateur
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la génération du token: " + e.getMessage());
       }

       // 5️⃣ Retourner le token à Angular
       return ResponseEntity.ok(Collections.singletonMap("token", token));
   }





    @GetMapping("/pending-logins")
        public ResponseEntity<List<LoginAttempt>> getPendingLoginAttempts() {
            List<LoginAttempt> pendingAttempts = userServices.findAllPendingAttempts();
            return ResponseEntity.ok(pendingAttempts);
        }

        // Endpoint pour accepter une tentative de connexion (par email)
        @PostMapping("/approve-login/{email}/{adminId}")
        public ResponseEntity<String> approveLogin(@PathVariable String email, @PathVariable Long adminId) {
            try {
                userServices.approveUserLogin(email, adminId); // Appelle le service pour approuver la connexion
                return ResponseEntity.ok("Connexion approuvée pour " + email);
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas les droits nécessaires.");
            }
        }


        // Endpoint pour bloquer une tentative de connexion (par email)
        @PostMapping("/block-login/{email}")
        public ResponseEntity<String> blockLogin(@PathVariable String email, @RequestBody Long adminId) {
            try {
                userServices.blockLoginAttempt(userServices.getPendingAttemptIdByEmail(email));  // Blocage basé sur l'email
                return ResponseEntity.ok("Connexion bloquée pour l'utilisateur : " + email);
            } catch (Exception e) {
                return ResponseEntity.status(400).body("Erreur lors du blocage de la connexion.");
            }
        }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
       emailService.sendVerificationCode(email);
        return ResponseEntity.ok("Code de vérification envoyé par email.");
    }

    // Endpoint pour vérifier le code et réinitialiser le mot de passe
    /*@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {
        userServices.resetPassword(email, code, newPassword);
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }*/
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {
        userServices.resetPassword(email, code, newPassword);

        // Création d'une map pour l'objet JSON de réponse
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe réinitialisé avec succès.");

        // Renvoi d'une réponse avec un code 200 et le message sous forme d'objet JSON
        return ResponseEntity.ok(response);
    }
    @PostMapping("add-recaptcha")
    @PermitAll
    public ResponseEntity<?> addUserWithCaptcha(@RequestBody Map<String, Object> payload) {
        User user = new User();
        // Récupérez les données utilisateur du payload
        user.setEmailU((String) payload.get("emailU"));
        user.setNomU((String) payload.get("nomU"));
        user.setPrenomU((String) payload.get("prenomU"));
        user.setMotdepasseU((String) payload.get("motdepasseU"));
        user.setRole(Role.ROLE_USER);

        String captchaResponse = (String) payload.get("captchaResponse");

        // Vérifiez reCAPTCHA ici
        if (!verifyCaptcha(captchaResponse)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de la vérification reCAPTCHA");
        }

        User addedUser = userServices.addUser(user);
        return ResponseEntity.ok(addedUser);
    }

    // Méthode pour vérifier le reCAPTCHA
    private boolean verifyCaptcha(String captchaResponse) {
        String secretKey = "6Le52MMpAAAAAHSKZwrk39k1yTY6tYd16M0CE3vE"; // Remplacez par votre clé secrète
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + secretKey + "&response=" + captchaResponse;

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
        Map<String, Object> body = response.getBody();
        return body != null && (Boolean) body.get("success");
    }


}



