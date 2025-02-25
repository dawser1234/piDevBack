package com.esprit.ms.pidevbackend.Service;

import com.esprit.ms.pidevbackend.Entity.Presence;
import com.esprit.ms.pidevbackend.Entity.User;
import com.esprit.ms.pidevbackend.Response.AuthResponse;

import java.util.List;

public interface IuserServices {
    public User addUser(User user);
    public List<User> getallUser();
    public User getUserbyId(Long id);
    public void deleteUser(Long id);
    public User UpdateUser(Long id ,User user);
    public Presence addPresence(Presence presence);
    public  void  deletepresence(Long id);
    public  Presence UpdatePresence(Long idp ,Presence presence);
    public Presence getPresenceByid(Long id);
    public List<Presence> getPresencesByid(Long id);
    public  User getUserByemail(String email);
    public AuthResponse login(User user);



}
