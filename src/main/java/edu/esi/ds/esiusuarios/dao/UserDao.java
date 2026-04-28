package edu.esi.ds.esiusuarios.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.esi.ds.esiusuarios.model.Usuario;

public interface UserDao extends JpaRepository<Usuario, Long> {

    @Query("SELECT u.username FROM usuario u WHERE u.session_token = :sessionToken")
    String checkUserToken(@Param("sessionToken") String sessionToken);  // Método para comprobar si el token de sesión es válido y devolver el nombre del usuario asociado a ese token

    @Query("SELECT u.username, u.email FROM usuario u WHERE u.session_token = :sessionToken")
    Object[] getUserInfoEmailByToken(@Param("sessionToken") String sessionToken);  // Método para obtener el email del usuario a partir de su token de sesión

    @Query("SELECT u.id FROM usuario u WHERE u.email = :mail AND u.password_hash = :passwordHash")
    java.util.Optional<Usuario> findByMailAndPasswordHash(@Param("mail") String mail, @Param("passwordHash") String passwordHash);  // Método para buscar un usuario por su email y contraseña (hash) y devolverlo como un Optional para manejar el caso de que no se encuentre
}