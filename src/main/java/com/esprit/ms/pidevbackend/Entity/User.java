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
    public User(Long idU) {
        this.idU = idU;
    }

}
