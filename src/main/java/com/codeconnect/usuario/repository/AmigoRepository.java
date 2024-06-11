package com.codeconnect.usuario.repository;

import com.codeconnect.usuario.model.UsuarioAmigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AmigoRepository extends JpaRepository<UsuarioAmigo, UUID> {
}