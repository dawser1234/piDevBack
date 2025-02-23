package com.esprit.ms.pidevbackend.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Presence {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long idP;
    Date dateP;
    LocalTime heureentre;
    LocalTime heuresortie;
    @ManyToOne
    @JoinColumn(name="user_id")
    private  User user;
}
