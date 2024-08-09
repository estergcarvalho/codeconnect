CREATE TABLE IF NOT EXISTS post_comentario (
    id                  UUID        PRIMARY KEY NOT NULL,
    id_post             UUID        NOT NULL,
    id_usuario          UUID        NOT NULL,
    descricao           TEXT        NOT NULL,
    data_criacao        TIMESTAMP   NOT NULL,

    CONSTRAINT fk_post_comentario_id_post       FOREIGN KEY (id_post)       REFERENCES post(id),
    CONSTRAINT fk_post_comentario_id_usuario    FOREIGN KEY (id_usuario)    REFERENCES usuario(id)
);