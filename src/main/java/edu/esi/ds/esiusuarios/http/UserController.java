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
import org.springframework.web.bind.annotation.RequestBody;

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


    // metodo para comporbar qu eel email esta correcto automaticamente
}
