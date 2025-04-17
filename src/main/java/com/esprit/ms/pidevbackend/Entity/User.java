package com.esprit.ms.pidevbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long idU;
     String nomU;
     String prenomU;
     String emailU;
     String motdepasseU;
     String numtel;
     float salaireU;
    @Enumerated(EnumType.STRING)
    Role role;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Presence>presences;
    String etude;
    String passion;
    String experience;
    String competences;

    // Champs calculés pour la progression
    @Transient // Utilisé pour éviter la persistance en base de données
            int profileCompletion;
    public User(Long idU) {
        this.idU = idU;
    }
    // Méthode pour calculer la progression du profil
    public int calculateProfileCompletion() {
        int progress = 0;
        if (this.nomU != null && !this.nomU.isEmpty()) progress += 10;
        if (this.prenomU != null && !this.prenomU.isEmpty()) progress += 10;
        if (this.emailU != null && !this.emailU.isEmpty()) progress += 10;
        if (this.etude != null && !this.etude.isEmpty()) progress += 20;
        if (this.passion != null && !this.passion.isEmpty()) progress += 20;
        if (this.experience != null && !this.experience.isEmpty()) progress += 20;
        if (this.competences != null && !this.competences.isEmpty()) progress += 10;

        return progress;
    }

    // Méthode pour savoir si le profil est vérifié (100% complété)
    public boolean isVerified() {
        return calculateProfileCompletion() == 100;
    }

}
