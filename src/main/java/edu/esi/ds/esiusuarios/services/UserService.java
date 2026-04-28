package edu.esi.ds.esiusuarios.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.esi.ds.esiusuarios.dao.UserDao;
import edu.esi.ds.esiusuarios.model.Usuario;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public String login(String mail, String password) {  // Autenticar al usuario y generar un token de sesión
        Usuario user = this.userDao.findByMailAndPasswordHash(mail, password).orElse(null);  // Buscamos el usuario por su email y contraseña (hash) y si no lo encontramos devolvemos null
        if(user != null) {
            String session_token = java.util.UUID.randomUUID().toString();
            user.setSessionToken(session_token);
            user.setLastLoginAt(java.time.LocalDateTime.now());
            this.userDao.save(user);
            return session_token;
        }
        return null;
    }

    public String checkUserToken(String sessionToken) {
        return this.userDao.checkUserToken(sessionToken);
    }

    public Object[] getUserInfoEmail(String sessionToken) {
        return this.userDao.getUserInfoEmailByToken(sessionToken);
    }
}   