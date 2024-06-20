package com.codeconnect.post.repository;

import com.codeconnect.post.dto.PostRecenteResponse;
import com.codeconnect.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = "SELECT p.id, u.id AS idUsuario, u.nome usuarioNome, p.descricao, p.data_criacao AS dataCriacao " +
        "FROM post p " +
        "INNER JOIN usuario_amigo ua ON p.id_usuario = ua.id_amigo " +
        "INNER JOIN usuario u ON p.id_usuario = u.id " +
        "WHERE ua.id_usuario = :usuarioId " +
        "ORDER BY p.data_criacao DESC", nativeQuery = true)
    List<PostRecenteResponse> recentes(UUID usuarioId);
}