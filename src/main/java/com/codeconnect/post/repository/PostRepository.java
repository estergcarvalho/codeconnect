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

    @Query(value = """
          SELECT DISTINCT(p.id),
              u.id           AS idUsuario,
              u.nome         AS usuarioNome,
              u.profissao    AS profissao,
              p.descricao,
              p.data_criacao AS dataCriacao,
              u.imagem       AS imagem,
              u.tipo_imagem  AS tipoImagem,
              CASE WHEN pc.id IS NOT NULL THEN TRUE ELSE FALSE END AS curtido
            FROM post p
               INNER JOIN usuario u ON p.id_usuario = u.id
               LEFT JOIN usuario_amigo ua ON ua.id_usuario = p.id_usuario
               LEFT JOIN post_curtida pc ON p.id = pc.id_post AND pc.id_usuario = :usuarioId
            WHERE ua.id_usuario = :usuarioId
              OR ua.id_amigo = :usuarioId
              OR p.id_usuario = :usuarioId
            ORDER BY p.data_criacao DESC;
        """, nativeQuery = true)
    List<PostRecenteResponse> recentes(UUID usuarioId);

    List<Post> findAllByUsuarioId(UUID usuarioId);

}