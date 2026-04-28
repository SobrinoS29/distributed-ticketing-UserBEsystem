package edu.esi.ds.esiusuarios.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esiusuarios.services.UserService;

@RestController
@RequestMapping("/external")
@CrossOrigin(origins = "*")
public class ExternalController {

    @Autowired
    private UserService userService;

    @GetMapping("/checkUserToken")
    public String checkToken(@RequestParam String sessionToken) {
        if(sessionToken == null || sessionToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error 400: Token is required");
        }
        String userCheck = this.userService.checkUserToken(sessionToken);
        if(userCheck == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error 401: Invalid token");
        }
        return userCheck;
    }

    @GetMapping("/getUserInfoEmail")
    public Object[] getUserInfoEmail(@RequestParam String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionToken is required");
        }
        Object[] data = userService.getUserInfoEmail(sessionToken);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalid");
        }
        return data;
    }

}
