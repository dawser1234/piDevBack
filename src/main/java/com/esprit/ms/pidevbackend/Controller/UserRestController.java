package com.esprit.ms.pidevbackend.Controller;
import com.esprit.ms.pidevbackend.Entity.Presence;
import com.esprit.ms.pidevbackend.Entity.User;
import com.esprit.ms.pidevbackend.Service.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/users")
@CrossOrigin(origins = "http://localhost:4200")

public class UserRestController {
    private  PasswordEncoder passwordEncoder;





    private  UserServices userServices;

    @PostMapping("add")
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
    public String login(@RequestBody User user) {
        User existingUser = userServices.getUserByemail(user.getEmailU());
        if (existingUser != null && passwordEncoder.matches(user.getMotdepasseU(), existingUser.getMotdepasseU())) {
            return "Login successful"; // Vous pouvez retourner un jeton JWT ou d'autres informations ici
        }
        return "Invalid credentials";
    }



}