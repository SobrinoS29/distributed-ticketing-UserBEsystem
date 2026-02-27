package edu.esi.ds.esiusuarios.services;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.esi.ds.esiusuarios.model.User;

@Service
public class UserService {

    private List<User> users;
    
    public UserService() {
        this.users = List.of(
            new User("Pepe", "pepe123", "token123"),
            new User("Ana", "ana123", "token456")
        );
    }

    public String login(String name, String password) {
        for (User user : this.users) {
            if(user.getName().equals(name) && user.getPassword().equals(password)) {
                return "Login successful for user: " + name;
            }
        }
        return null;  // Si no se encuentra el usuario o la contraseña es incorrecta, devolvemos null y lo comprobamos en el controlador para lanzar la excepción adecuada
    }

    public String checkToken(String token) {
        for (User user : this.users) {
            if(user.getToken().equals(token)) {
                return user.getName();
            }
        }
        return null;  // Si no se encuentra el token, devolvemos null y lo comprobamos en el controlador para lanzar la excepción adecuada
    }
}