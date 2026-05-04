package edu.esi.ds.esiusuarios.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;

import edu.esi.ds.esiusuarios.dao.OrionDao;
import edu.esi.ds.esiusuarios.dao.UserDao;
import edu.esi.ds.esiusuarios.dao.PasswordResetTokenDao;
import edu.esi.ds.esiusuarios.model.Orion;
import edu.esi.ds.esiusuarios.model.Usuario;
import edu.esi.ds.esiusuarios.model.PasswordResetToken;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private OrionDao orionDao;

    @Autowired
    private PasswordResetTokenDao passwordResetTokenDao;

    @Value("${esientradas.service.url:http://localhost:8080}")
    private String esiEntradosServiceUrl;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RestTemplate restTemplate;

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

    @Transactional
    public String requestPasswordReset(String email, String frontendUrl) {
        Usuario user = this.userDao.findByEmail(email).orElse(null);
        if (user == null) {
            // Por seguridad, no revelamos si el email existe o no
            return "Se ha enviado un correo de recuperación si el email existe en nuestros registros";
        }

        // Generar token único
        String resetToken = java.util.UUID.randomUUID().toString();

        // Establecer expiración de 1 hora
        java.time.LocalDateTime expiryDate = java.time.LocalDateTime.now().plusHours(1);

        // Eliminar tokens antiguos del usuario
        this.passwordResetTokenDao.deleteByUsuario_IdAndExpiryDateBefore(user.getId(), java.time.LocalDateTime.now());

        // Guardar nuevo token
        PasswordResetToken token = new PasswordResetToken(resetToken, user, expiryDate);
        this.passwordResetTokenDao.save(token);

        // Enviar correo a través de esiEntradas
        try {
            String emailUrl = esiEntradosServiceUrl + "/email/enviar-recuperacion-contrasena";
            Map<String, String> emailRequest = new HashMap<>();
            emailRequest.put("email", email);
            emailRequest.put("resetToken", resetToken);
            emailRequest.put("frontendUrl", frontendUrl);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(emailRequest);
            restTemplate.exchange(emailUrl, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo enviar el correo de recuperación: " + e.getMessage(), e);
        }

        return "Se ha enviado un correo de recuperación si el email existe en nuestros registros";
    }

    @Transactional
    public boolean validateResetToken(String token) {
        PasswordResetToken resetToken = this.passwordResetTokenDao.findByToken(token).orElse(null);
        if (resetToken == null) {
            return false;
        }
        return !resetToken.isExpired();
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = this.passwordResetTokenDao.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.isExpired()) {
            return false;
        }

        Usuario user = resetToken.getUsuario();
        String encodedPassword = this.passwordEncoder.encode(newPassword);

        // Actualizar contraseña en Orion
        Orion credential = this.orionDao.findByUsuarioEmail(user.getEmail()).orElse(null);
        if (credential != null) {
            credential.setAccessHash(encodedPassword);
            this.orionDao.save(credential);
        }

        // Eliminar token usado
        this.passwordResetTokenDao.delete(resetToken);

        // Enviar correo de confirmación a través de esiEntradas
        try {
            String emailUrl = esiEntradosServiceUrl + "/email/enviar-cambio-contrasena";
            Map<String, String> emailRequest = new HashMap<>();
            emailRequest.put("email", user.getEmail());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(emailRequest);
            restTemplate.exchange(emailUrl, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo enviar el correo de confirmación: " + e.getMessage(), e);
        }

        return true;
    }
}   