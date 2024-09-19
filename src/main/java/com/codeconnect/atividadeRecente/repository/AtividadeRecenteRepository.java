package com.codeconnect.atividadeRecente.repository;

import com.codeconnect.atividadeRecente.enums.AtividadeEnum;
import com.codeconnect.atividadeRecente.model.AtividadeRecente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtividadeRecenteRepository extends JpaRepository<AtividadeRecente, UUID> {

    Optional<AtividadeRecente> findByUsuarioIdAndPostIdAndAtividade(UUID usuarioId, UUID postId, AtividadeEnum atividadeEnum);

}