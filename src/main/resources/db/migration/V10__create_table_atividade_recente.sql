CREATE TABLE IF NOT EXISTS atividade_recente (
     id                  UUID        PRIMARY KEY NOT NULL,
     id_post             UUID        NOT NULL,
     id_usuario          UUID        NOT NULL,
     atividade           INT         NOT NULL,
     data_criacao        TIMESTAMP   NOT NULL,

    CONSTRAINT fk_atividade_recente_id_usuario    FOREIGN KEY (id_usuario)    REFERENCES usuario(id),
    CONSTRAINT fk_atividade_recente_id_post       FOREIGN KEY (id_post)       REFERENCES post(id)
);