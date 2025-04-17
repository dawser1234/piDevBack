package com.esprit.ms.pidevbackend.Repository;

import com.esprit.ms.pidevbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByEmailU(String email);
     User findUserByIdU(Long idU) ;
    User save(User user);


}
