package com.codeconnect.post.model;

import com.codeconnect.usuario.model.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @Column(name = "data_criacao")
    private Timestamp dataCriacao;

    private String descricao;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCurtida> curtidas;

}