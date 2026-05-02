package edu.esi.ds.esiusuarios.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.esi.ds.esiusuarios.dao.OrionDao;
import edu.esi.ds.esiusuarios.dao.UserDao;
import edu.esi.ds.esiusuarios.model.Orion;
import edu.esi.ds.esiusuarios.model.Usuario;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private OrionDao orionDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public String login(String mail, String password) {  // Autenticar al usuario y generar un token de sesión
        Orion credential = this.orionDao.findByUsuarioEmail(mail).orElse(null);
        if (credential != null && this.passwordEncoder.matches(password, credential.getAccessHash())) {
            Usuario user = credential.getUsuario();
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

    @Transactional
    public boolean register(String name, String email, String password) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        String normalizedName = name.trim();
        String normalizedEmail = email.trim();

        if (this.userDao.existsByUsername(normalizedName) || this.userDao.existsByEmail(normalizedEmail)) {
            return false;
        }

        Usuario user = new Usuario(normalizedName, normalizedEmail);
        this.userDao.save(user);

        String encodedPassword = this.passwordEncoder.encode(password);
        Orion credential = new Orion(user, encodedPassword);
        this.orionDao.save(credential);
        return true;
    }
}   