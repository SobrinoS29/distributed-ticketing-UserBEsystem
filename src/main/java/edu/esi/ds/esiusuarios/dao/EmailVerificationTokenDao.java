package edu.esi.ds.esiusuarios.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.esi.ds.esiusuarios.model.EmailVerificationToken;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenDao extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUsuario_IdAndExpiryDateBefore(Long usuarioId, LocalDateTime expiryDate);
}
