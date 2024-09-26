package com.codeconnect.atividaderecente.repository;

import com.codeconnect.atividaderecente.enums.AtividadeEnum;
import com.codeconnect.atividaderecente.model.AtividadeRecente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtividadeRecenteRepository extends JpaRepository<AtividadeRecente, UUID> {

    Optional<AtividadeRecente> findByUsuarioIdAndPostIdAndAtividade(UUID usuarioId, UUID postId, AtividadeEnum atividadeEnum);

    List<AtividadeRecente> findByUsuarioIdAndAtividadeIn(UUID usuarioId, List<AtividadeEnum> atividades);

}