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
            String userToken = java.util.UUID.randomUUID().toString();
            user.setUserToken(userToken);
            user.setLastLoginAt(java.time.LocalDateTime.now());
            this.userDao.save(user);
            return userToken;
        }
        return null;
    }

    public String checkUserToken(String userToken) {
        return this.userDao.checkUserToken(userToken);
    }

    public Object[] getUserInfoEmail(String userToken) {
        return this.userDao.getUserInfoEmailByToken(userToken);
    }

    public void register(String name, String email, String password) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return;
        }

        String normalizedName = name.trim();
        String normalizedEmail = email.trim();

        if (this.userDao.existsByUsername(normalizedName) || this.userDao.existsByEmail(normalizedEmail)) {
            return;
        }

        Usuario user = new Usuario(normalizedName, password, normalizedEmail);
        this.userDao.save(user);
        return;
    }
}   