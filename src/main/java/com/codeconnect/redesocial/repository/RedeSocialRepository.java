package com.codeconnect.redesocial.repository;

import com.codeconnect.redesocial.model.RedeSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RedeSocialRepository extends JpaRepository<RedeSocial, UUID> {
}