package com.codeconnect.post.repository;

import com.codeconnect.post.model.Post;
import com.codeconnect.post.model.PostComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostComentarioRepository extends JpaRepository<PostComentario, UUID> {

    long countByPost(Post post);

    List<PostComentario> findAllByPostId(UUID postId);

}