CREATE TABLE IF NOT EXISTS post_curtida (
    id              UUID        PRIMARY KEY NOT NULL,
    id_post         UUID        NOT NULL,
    id_usuario      UUID        NOT NULL,
    data            TIMESTAMP   NOT NULL,

    CONSTRAINT fk_post_curtida_id_post     FOREIGN KEY (id_post)    REFERENCES post(id),
    CONSTRAINT fk_post_curtida_id_usuario  FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT unique_post_usuario UNIQUE (id_post, id_usuario)
);