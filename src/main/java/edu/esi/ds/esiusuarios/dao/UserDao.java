package edu.esi.ds.esiusuarios.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.esi.ds.esiusuarios.model.Usuario;

public interface UserDao extends JpaRepository<Usuario, Long> {

    @Query("SELECT u.username FROM Usuario u WHERE u.token = :userToken")
    String checkUserToken(@Param("userToken") String userToken);  // Método para comprobar si el token de sesión es válido y devolver el nombre del usuario asociado a ese token

    @Query("SELECT u.username, u.email FROM Usuario u WHERE u.token = :userToken")
    Object[] getUserInfoEmailByToken(@Param("userToken") String userToken);  // Método para obtener el email del usuario a partir de su token de sesión
}