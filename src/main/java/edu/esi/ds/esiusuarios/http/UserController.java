package edu.esi.ds.esiusuarios.http;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import edu.esi.ds.esiusuarios.services.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials) {  // Recibimos: {"mail": "pepe@gmail.com", "pwd": "pepe123"}
        JSONObject jsonCredentials = new JSONObject(credentials);  // Convertimos el Map a un JSONObject para facilitar el acceso a los campos
            String mail = jsonCredentials.optString("mail");  // Si no está dara la cadena vacio y no .getString() que lanzaría error 500
            String password = jsonCredentials.optString("pwd");

        if(mail.isEmpty() || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error 403: Invalid Credentials");
        }
        String result = this.userService.login(mail, password);
        if(result == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error 403: Invalid Credentials");
        }
        return result;
    }

    @PostMapping("/register")
    public void register(@RequestBody Map<String, String> credentials) {
        JSONObject jsonCredentials = new JSONObject(credentials);
        String name = jsonCredentials.optString("name").trim();
        String email = jsonCredentials.optString("email").trim();
        String password = jsonCredentials.optString("pwd");

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error 400: Missing registration data");
        }
        boolean success = this.userService.register(name, email, password);
        if (success) {
            // Enviaremos un mesnaje al correo de confirmación de registro
        }
        return;
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error 400: Email is required");
        }
        
        String frontendUrl = request.getOrDefault("frontendUrl", "http://localhost:4200");
        String message = this.userService.requestPasswordReset(email.trim(), frontendUrl);
        return Map.of("message", message);
    }

    @GetMapping("/reset-password/validate")
    public Map<String, Boolean> validateResetToken(@RequestParam String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error 400: Token is required");
        }
        
        boolean isValid = this.userService.validateResetToken(token);
        return Map.of("valid", isValid);
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error 400: Token and password are required");
        }

        boolean success = this.userService.resetPassword(token, newPassword);
        if (!success) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error 401: Invalid or expired token");
        }

        return Map.of("message", "Contraseña actualizada con éxito");
    }
}
