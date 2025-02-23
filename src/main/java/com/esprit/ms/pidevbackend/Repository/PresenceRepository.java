package com.esprit.ms.pidevbackend.Repository;

import com.esprit.ms.pidevbackend.Entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface PresenceRepository extends JpaRepository<Presence,Long> {
    List<Presence> findByUser_IdU(Long userId);
}
