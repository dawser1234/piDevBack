package com.esprit.ms.pidevbackend.Controller;
import com.esprit.ms.pidevbackend.Entity.Presence;
import com.esprit.ms.pidevbackend.Entity.User;
import com.esprit.ms.pidevbackend.Response.AuthResponse;
import com.esprit.ms.pidevbackend.Service.UserServices;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:4200")

public class UserRestController {
    private  PasswordEncoder passwordEncoder;





    private  UserServices userServices;


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

    @PutMapping("/update/user/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userServices.UpdateUser(id, user);
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



}