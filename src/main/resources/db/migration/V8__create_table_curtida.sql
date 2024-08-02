CREATE TABLE IF NOT EXISTS curtida (
    id              UUID        PRIMARY KEY NOT NULL,
    id_post         UUID        NOT NULL,
    id_usuario      UUID        NOT NULL,
    data            TIMESTAMP   NOT NULL,

    CONSTRAINT fk_curtida_post     FOREIGN KEY (id_post)    REFERENCES post(id),
    CONSTRAINT fk_curtida_usuario  FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT unique_post_usuario UNIQUE (id_post, id_usuario)
);