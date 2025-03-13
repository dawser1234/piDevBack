package com.esprit.ms.pidevbackend.Repository;

import com.esprit.ms.pidevbackend.Entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt,Long> {
    List<LoginAttempt> findByAccepted(boolean accepted);

    // Méthode pour trouver une tentative de connexion par email
    LoginAttempt findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
