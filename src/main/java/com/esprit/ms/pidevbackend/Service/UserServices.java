    package com.esprit.ms.pidevbackend.Service;

    import com.esprit.ms.pidevbackend.Entity.Presence;
    import com.esprit.ms.pidevbackend.Entity.User;
    import com.esprit.ms.pidevbackend.Repository.PresenceRepository;
    import com.esprit.ms.pidevbackend.Repository.UserRepository;
    import lombok.AllArgsConstructor;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @AllArgsConstructor

    public class UserServices implements IuserServices {

        UserRepository userRepository;
        PresenceRepository presenceRepository;
        private  PasswordEncoder passwordEncoder;

        @Override
        public User addUser(User user) {
            user.setMotdepasseU(passwordEncoder.encode(user.getMotdepasseU()));
            return userRepository.save(user);
        }

        @Override
        public List<User> getallUser() {
            return userRepository.findAll();
        }

        @Override
        public User getUserbyId(Long id) {
            return userRepository.findById(id).get();
        }

        @Override
        public void deleteUser(Long id) {
             userRepository.deleteById(id);
        }

       /* @Override
        public User UpdateUser(Long id,User user) {
            // Vérifier si l'utilisateur existe
            if (userRepository.existsById(id)) {
                // Si le mot de passe est modifié, le hacher
                if (user.getMotdepasseU() != null) {
                    user.setMotdepasseU(passwordEncoder.encode(user.getMotdepasseU()));
                }

                // Définir l'ID de l'utilisateur pour la mise à jour
                user.setIdU(id);

                return userRepository.save(user);
            }

            return null; // Retourner null si l'utilisateur n'existe pas
        }*/
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
               if (user.getMotdepasseU() != null) {
                   existingUser.setMotdepasseU(passwordEncoder.encode(user.getMotdepasseU()));
               }
               if (user.getRole()!=null){
                   existingUser.setRole(user.getRole());
               }

               // Sauvegarder l'utilisateur mis à jour
               return userRepository.save(existingUser);
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

       /* @Override
        public Presence UpdatePresence(Long idp, Presence presence) {
            // Vérifier si la présence existe
            if (presenceRepository.existsById(idp)) {
                // Définir l'ID de la présence pour la mise à jour
                presence.setIdP(idp);

                return presenceRepository.save(presence);
            }

            return null; // Retourner null si la présence n'existe pas
        }*/
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


    }
