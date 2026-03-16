package edu.esi.ds.esiusuarios.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.esi.ds.esiusuarios.dao.UserDao;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    /*
    public String login(String name, String password) {
        for (User user : this.users) {
            if(user.getName().equals(name) && user.getPassword().equals(password)) {
                String sessionToken = UUID.randomUUID().toString();
                user.setToken(sessionToken);
                return sessionToken;
            }
        }
        return null;  // Si no se encuentra el usuario o la contraseña es incorrecta, devolvemos null y lo comprobamos en el controlador para lanzar la excepción adecuada
    }
    */

    public String checkUserToken(String userToken) {
        return this.userDao.checkUserToken(userToken);
    }

    public Object[] getUserInfoEmail(String userToken) {
        return this.userDao.getUserInfoEmailByToken(userToken);
    }

    public String registrar() {
        return "Registro exitoso";
    }
}