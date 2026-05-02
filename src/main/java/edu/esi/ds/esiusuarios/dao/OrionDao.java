package edu.esi.ds.esiusuarios.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.esi.ds.esiusuarios.model.Orion;

public interface OrionDao extends JpaRepository<Orion, Long> {

    Optional<Orion> findByUsuarioEmail(String email);
}