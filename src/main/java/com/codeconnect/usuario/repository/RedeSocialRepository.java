package com.codeconnect.usuario.repository;

import com.codeconnect.usuario.model.RedeSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RedeSocialRepository extends JpaRepository<RedeSocial, UUID> {
}