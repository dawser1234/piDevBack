    package com.esprit.ms.pidevbackend.Service;

    import com.esprit.ms.pidevbackend.Config.JwtTokenProvider;
    import com.esprit.ms.pidevbackend.Entity.LoginAttempt;
    import com.esprit.ms.pidevbackend.Entity.Presence;
    import com.esprit.ms.pidevbackend.Entity.Role;
    import com.esprit.ms.pidevbackend.Entity.User;
    import com.esprit.ms.pidevbackend.Repository.LoginAttemptRepository;
    import com.esprit.ms.pidevbackend.Repository.PresenceRepository;
    import com.esprit.ms.pidevbackend.Repository.UserRepository;
    import com.esprit.ms.pidevbackend.Response.AuthResponse;
    import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
    import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
    import com.google.api.client.http.javanet.NetHttpTransport;
    import com.google.gson.JsonArray;
    import com.google.gson.JsonElement;
    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import lombok.AllArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;

    import java.io.IOException;
    import java.security.GeneralSecurityException;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.util.*;


    @Service
    @AllArgsConstructor

    public class UserServices implements IuserServices {

        UserRepository userRepository;
        PresenceRepository presenceRepository;
         LoginAttemptRepository loginAttemptRepository;
        private JwtTokenProvider jwtTokenProvider;
        private  PasswordEncoder passwordEncoder;
        private EmailService emailService;

        /*@Override
        public User addUser(User user) {
            user.setMotdepasseU(passwordEncoder.encode(user.getMotdepasseU()));
            return userRepository.save(user);
        }*/
        @Override
        public User addUser(User user) {
            // Vérifier si un utilisateur avec le même email existe déjà
            User existingUser = userRepository.findUserByEmailU(user.getEmailU());
            if (existingUser != null) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Encoder le mot de passe
            user.setMotdepasseU(passwordEncoder.encode(user.getMotdepasseU()));

            // Sauvegarder l'utilisateur
            return userRepository.save(user);
        }


        @Override
        public List<User> getallUser() {
            return userRepository.findAll();
        }



        /*@Override
        public User getUserbyId(Long id) {
            return userRepository.findById(id).get();
        }*/
        public User getUserbyId(Long id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec l'ID: " + id));
        }


        @Override
        public void deleteUser(Long id) {
             userRepository.deleteById(id);
        }


       @Override
       public User UpdateUser(Long id, User user) {
           // Vérifier si l'utilisateur existe
           if (userRepository.existsById(id)) {
               // Récupérer l'utilisateur existant
               User existingUser = userRepository.findById(id).get();

               // Mettre à jour uniquement les champs qui sont fournis
               if (user.getNomU() != null) {
                   existingUser.setNomU(user.getNomU());
               }
               if (user.getPrenomU() != null) {
                   existingUser.setPrenomU(user.getPrenomU());
               }
               if (user.getEmailU() != null) {
                   existingUser.setEmailU(user.getEmailU());
               }
               if (user.getSalaireU() != 0.0) { // Vérification du salaire
                   existingUser.setSalaireU(user.getSalaireU());
               }
               /*if (user.getMotdepasseU() != null && !user.getMotdepasseU().isEmpty()) {
                   existingUser.setMotdepasseU(passwordEncoder.encode(user.getMotdepasseU())); // Encoder seulement si une nouvelle valeur est fournie
               }*/
               if (user.getMotdepasseU() != null && !user.getMotdepasseU().isEmpty()) {
                   String motDePasseRecu = user.getMotdepasseU();  // Déjà encodé
                   String motDePasseEnBase = existingUser.getMotdepasseU(); // Déjà encodé

                   if (!motDePasseRecu.equals(motDePasseEnBase)) {
                       System.out.println("Mot de passe différent, MAJ en cours...");
                       existingUser.setMotdepasseU(motDePasseRecu);
                   } else {
                       System.out.println("Mot de passe inchangé !");
                   }
               }

               if (user.getRole()!=null){
                   existingUser.setRole(user.getRole());
               }

               // Sauvegarder l'utilisateur mis à jour
                User updatedUser = userRepository.save(existingUser);
               String token = jwtTokenProvider.generateToken(updatedUser.getEmailU(), updatedUser.getRole().name(),updatedUser.getIdU());
               System.out.println("Nouveau token généré : " + token);

               return updatedUser;

           }

           // Retourner null si l'utilisateur n'existe pas
           return null;
       }



        @Override
        public Presence addPresence(Presence presence) {
            return presenceRepository.save(presence);
        }

        @Override
        public void deletepresence(Long id) {
            presenceRepository.deleteById(id);

        }

        @Override
        public List<Presence> getallpresence() {
            return presenceRepository.findAll();
        }


        @Override
       public Presence UpdatePresence(Long idp, Presence presence) {
           // Vérifier si la présence existe
           if (presenceRepository.existsById(idp)) {
               // Récupérer l'entité existante
               Presence existingPresence = presenceRepository.findById(idp).get();

               // Mettre à jour uniquement les champs qui sont fournis
               if (presence.getDateP() != null) {
                   existingPresence.setDateP(presence.getDateP());
               }
               if (presence.getHeureentre() != null) {
                   existingPresence.setHeureentre(presence.getHeureentre());
               }
               if (presence.getHeuresortie() != null) {
                   existingPresence.setHeuresortie(presence.getHeuresortie());
               }

               // Sauvegarder l'entité mise à jour
               return presenceRepository.save(existingPresence);
           }

           // Retourner null si la présence n'existe pas
           return null;
       }



        @Override
        public Presence getPresenceByid(Long id) {
            return presenceRepository.findById(id).get();
        }

        @Override
        public List<Presence> getPresencesByid(Long id) {
            return presenceRepository.findByUser_IdU(id);
        }

        @Override
        public User getUserByemail(String email) {
            return userRepository.findUserByEmailU(email);
        }


        /*@Override
        public AuthResponse login(User user) {
            // Trouver l'utilisateur par email
            User existingUser = userRepository.findUserByEmailU(user.getEmailU());

            // Vérifier si l'utilisateur existe et si le mot de passe correspond
            if (existingUser != null) {
                boolean passwordMatches = passwordEncoder.matches(user.getMotdepasseU(), existingUser.getMotdepasseU());
                if (passwordMatches) {
                    // Récupérer le rôle à jour
                    String role = existingUser.getRole().name();
                    System.out.println("Connexion réussie pour l'utilisateur : " + existingUser.getEmailU() + ", Rôle : " + role); // Pour débogage

                    // Générer le token JWT
                    String token = jwtTokenProvider.generateToken(existingUser.getEmailU(), role);
                    return new AuthResponse(token); // Retourner l'AuthResponse avec le token
                } else {
                    System.out.println("Mot de passe incorrect pour l'utilisateur : " + user.getEmailU());
                }
            } else {
                System.out.println("Utilisateur non trouvé : " + user.getEmailU());
            }

            // Retourner null ou lancer une exception pour les identifiants invalides
            return null; // ou vous pouvez lever une exception personnalisée ici
        }
        public void saveLoginAttempt(String email) {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setEmail(email);
            attempt.setAccepted(false); // Par défaut, la tentative est en attente
            loginAttemptRepository.save(attempt);
        }

        // Méthode pour accepter une tentative de connexion
        public void acceptLoginAttempt(Long id) {
            LoginAttempt attempt = loginAttemptRepository.findById(id).orElseThrow();
            attempt.setAccepted(true);
            loginAttemptRepository.save(attempt);
        }

        // Méthode pour bloquer une tentative de connexion
        public void blockLoginAttempt(Long id) {
            LoginAttempt attempt = loginAttemptRepository.findById(id).orElseThrow();
            attempt.setAccepted(false);
            loginAttemptRepository.save(attempt);
        }

        // Méthode pour récupérer toutes les tentatives en attente
        public List<LoginAttempt> findAllPendingAttempts() {
            return loginAttemptRepository.findByAccepted(false);
        }

        // Méthode pour authentifier un utilisateur
        public ResponseEntity<?> login(LoginRequest loginRequest) {
            // Authentifiez l'utilisateur (vérifiez l'email et le mot de passe)

            // Si l'authentification réussit
            saveLoginAttempt(loginRequest.getEmail());

            // Retourner un message indiquant que la connexion est en attente
            return ResponseEntity.ok("Connexion réussie, en attente d'approbation.");
        }*/

       /* @Override
        public AuthResponse login(User user) {
            User existingUser = userRepository.findUserByEmailU(user.getEmailU());

            if (existingUser != null) {
                boolean passwordMatches = passwordEncoder.matches(user.getMotdepasseU(), existingUser.getMotdepasseU());
                if (passwordMatches) {
                    // Enregistrer la tentative de connexion
                    saveLoginAttempt(existingUser.getEmailU());

                    // Vérifier si l'utilisateur est accepté
                    if (!isLoginAccepted(existingUser.getEmailU())) {
                        System.out.println("Connexion en attente d'approbation pour l'utilisateur : " + existingUser.getEmailU());
                        return null; // ou lancer une exception
                    }

                    // Générer le token JWT
                    String token = jwtTokenProvider.generateToken(existingUser.getEmailU(), existingUser.getRole().name());
                    return new AuthResponse(token);
                } else {
                    System.out.println("Mot de passe incorrect pour l'utilisateur : " + user.getEmailU());
                }
            } else {
                System.out.println("Utilisateur non trouvé : " + user.getEmailU());
            }
            return null; // ou vous pouvez lever une exception personnalisée ici
        }*/
       /*@Override
       public AuthResponse login(User user) {
           User existingUser = userRepository.findUserByEmailU(user.getEmailU());

           if (existingUser == null) {
               System.out.println("Utilisateur non trouvé : " + user.getEmailU());
               saveLoginAttempt(user.getEmailU());
               return new AuthResponse("Utilisateur non trouvé.");
           }

           // Vérification du mot de passe
           boolean passwordMatches = passwordEncoder.matches(user.getMotdepasseU(), existingUser.getMotdepasseU());

           if (!passwordMatches) {
               System.out.println("Mot de passe incorrect pour l'utilisateur : " + user.getEmailU());
               saveLoginAttempt(user.getEmailU());
               return new AuthResponse("Mot de passe incorrect.");
           }

           // Vérification du rôle
           if (existingUser.getRole() == Role.ROLE_ADMIN) {
               String token = jwtTokenProvider.generateToken(existingUser.getEmailU(), existingUser.getRole().name());
               return new AuthResponse(token);
           } else {
               // Vérification si la connexion a été acceptée
               if (!isLoginAccepted(existingUser.getEmailU())) {
                   System.out.println("Connexion en attente d'approbation pour l'utilisateur : " + existingUser.getEmailU());
                   return new AuthResponse("Votre connexion est en attente d'approbation.");
               }

               // Générer un token JWT uniquement si l'utilisateur a été approuvé
               String token = jwtTokenProvider.generateToken(existingUser.getEmailU(), existingUser.getRole().name());
               return new AuthResponse(token);
           }
       }*/
       @Override
       public AuthResponse login(User user) {
           // Vérifiez si l'utilisateur existe
           User existingUser = userRepository.findUserByEmailU(user.getEmailU());

           if (existingUser == null) {
               System.out.println("Utilisateur non trouvé : " + user.getEmailU());
               saveLoginAttempt(user.getEmailU());
               return new AuthResponse("Utilisateur non trouvé.");
           }

           // Vérification du mot de passe
           boolean passwordMatches = passwordEncoder.matches(user.getMotdepasseU(), existingUser.getMotdepasseU());

           if (!passwordMatches) {
               System.out.println("Mot de passe incorrect pour l'utilisateur : " + user.getEmailU());
               saveLoginAttempt(user.getEmailU());
               return new AuthResponse("Mot de passe incorrect.");
           }

           // Vérification du rôle de l'utilisateur
           if (existingUser.getRole() == Role.ROLE_ADMIN) {
               // Si l'utilisateur est un administrateur, générer le token directement
               String token = jwtTokenProvider.generateToken(existingUser.getEmailU(), existingUser.getRole().name(),existingUser.getIdU());
               return new AuthResponse(token);
           }

           // Vérification si la connexion a été acceptée
           if (!isLoginAccepted(existingUser.getEmailU())) {
               System.out.println("Connexion en attente d'approbation pour l'utilisateur : " + existingUser.getEmailU());
               saveLoginAttempt(existingUser.getEmailU()); // Enregistrer la tentative même si elle est en attente d'approbation
               return new AuthResponse("Votre connexion est en attente d'approbation.");
           }

           // Si tout est correct, générer le token JWT
           String token = jwtTokenProvider.generateToken(existingUser.getEmailU(), existingUser.getRole().name(),existingUser.getIdU());
           return new AuthResponse(token);
       }

       // Service pour envoyer des emails


        public void saveLoginAttempt(String email) {
            // Vérifier si une tentative existe déjà pour cet utilisateur
            LoginAttempt attempt = loginAttemptRepository.findByEmail(email);
            if (attempt == null) {
                attempt = new LoginAttempt();
                attempt.setEmail(email);
                attempt.setAccepted(false);
                loginAttemptRepository.save(attempt);
                System.out.println("Nouvelle tentative de connexion enregistrée pour " + email);
            } else {
                System.out.println("Une tentative existe déjà pour cet utilisateur.");
            }
        }

        public void acceptLoginAttempt(Long id) {
            LoginAttempt attempt = loginAttemptRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tentative de connexion non trouvée"));

            attempt.setAccepted(true);
            loginAttemptRepository.save(attempt);
            System.out.println("Connexion approuvée pour l'utilisateur : " + attempt.getEmail());
        }

        public List<LoginAttempt> findAllPendingAttempts() {
            return loginAttemptRepository.findByAccepted(false);
        }

        private boolean isLoginAccepted(String email) {
            LoginAttempt attempt = loginAttemptRepository.findByEmail(email);
            if (attempt == null) {
                System.out.println("Aucune tentative trouvée pour " + email);
                return false;
            }
            System.out.println("Statut de la tentative de " + email + " : " + attempt.isAccepted());
            return attempt.isAccepted();
        }

        public void approveUserLogin(String email, Long adminId) {
            // Vérifie si l'admin existe
            User admin = getUserbyId(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Utilisateur administrateur introuvable.");
            }

            // Vérifie si l'admin a bien le rôle ADMIN
            if (admin.getRole() != Role.ROLE_ADMIN) {
                throw new SecurityException("L'utilisateur n'a pas les droits nécessaires.");
            }

            // Récupère l'ID de la tentative de connexion à partir de l'email
            Long attemptId = getPendingAttemptIdByEmail(email);
            if (attemptId == null) {
                throw new IllegalArgumentException("Tentative de connexion introuvable pour l'email : " + email);
            }

            // Accepte la tentative de connexion
            acceptLoginAttempt(attemptId);
        }


        public Long getPendingAttemptIdByEmail(String email) {
            LoginAttempt attempt = loginAttemptRepository.findByEmail(email);
            return attempt != null ? attempt.getId() : null;
        }
        public void blockLoginAttempt(Long id) {
            LoginAttempt attempt = loginAttemptRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tentative de connexion non trouvée"));
            attempt.setAccepted(false);
            loginAttemptRepository.save(attempt);
        }



        public void resetPassword(String email, String code, String newPassword) {
            User user = userRepository.findUserByEmailU(email);
            if (user != null && emailService.verifyCode(email, code)) {
                user.setMotdepasseU(passwordEncoder.encode(newPassword)); // Encoder le nouveau mot de passe
                emailService.clearCode(email); // Effacer le code après utilisation
                userRepository.save(user);
            } else {
                throw new RuntimeException("Code de vérification invalide ou email non trouvé.");
            }
        }
        public User getUserById(Long idU) {
            return userRepository.findUserByIdU(idU);

        }
        public User save(User user) {
            return userRepository.save(user);
            // Sauvegarder l'utilisateur
        }
        /*public Presence addPresenceWithHolidayCheck(Presence presence) {
            // Conversion de dateP en LocalDate
            LocalDate presenceDate = presence.getDateP().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            int year = presenceDate.getYear();
            String countryCode = "DE";  // Code pour la Tunisie

            // Construire l'URL pour l'API Nager.Date
            String url = String.format("https://date.nager.at/api/v3/PublicHolidays/%d/%s", year, countryCode);

            // Création d'un RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // Récupérer la réponse de l'API sous forme de chaîne JSON
            String response = restTemplate.getForObject(url, String.class);

            // Parsez la réponse JSON sans créer de modèle spécifique (utilisation de Gson ici)
            boolean isHoliday = false;
            if (response != null) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonObject responseObj = jsonObject.get("response").getAsJsonObject();
                if (responseObj.has("holidays")) {
                    for (JsonElement holidayElement : responseObj.get("holidays").getAsJsonArray()) {
                        JsonObject holidayObj = holidayElement.getAsJsonObject();
                        // Récupérer la date en ISO format (ex: "2025-01-01")
                        String holidayDateIso = holidayObj.get("date").getAsJsonObject().get("iso").getAsString();
                        if (presenceDate.toString().equals(holidayDateIso)) {
                            isHoliday = true;
                            break;
                        }
                    }
                }
            }

            if (isHoliday) {
                System.out.println("La date " + presence.getDateP() + " est un jour férié en Tunisie.");
                // Choix de la logique :
                // soit ne pas enregistrer cette présence,
                // soit la marquer différemment
                // Ici, on continue et on enregistre, mais vous pouvez adapter.
            }

            return presenceRepository.save(presence);
        }*/
        public Presence addPresenceWithHolidayCheck(Presence presence) {
            // Conversion de dateP en LocalDate
            LocalDate presenceDate = presence.getDateP().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            int year = presenceDate.getYear();
            String countryCode = "DE";  // 🇹🇳 Code pour la Tunisie (attention : tu avais "DE")

            // Construire l'URL pour l'API Nager.Date
            String url = String.format("https://date.nager.at/api/v3/PublicHolidays/%d/%s", year, countryCode);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            boolean isHoliday = false;
            if (response != null) {
                JsonArray holidaysArray = JsonParser.parseString(response).getAsJsonArray();

                for (JsonElement holidayElement : holidaysArray) {
                    JsonObject holidayObj = holidayElement.getAsJsonObject();
                    String holidayDate = holidayObj.get("date").getAsString();

                    if (presenceDate.toString().equals(holidayDate)) {
                        isHoliday = true;
                        break;
                    }
                }
            }

            if (isHoliday) {
                System.out.println("La date " + presence.getDateP() + " est un jour férié en Tunisie.");
                throw new IllegalArgumentException("Impossible d'enregistrer une présence un jour férié.");
                // Ici tu peux soit refuser l'enregistrement, soit le marquer comme "jour férié", etc.
            }

            return presenceRepository.save(presence);
        }





    }
