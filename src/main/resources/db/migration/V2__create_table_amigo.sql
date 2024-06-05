CREATE TABLE IF NOT EXISTS amigo (
    id              UUID        PRIMARY KEY NOT NULL,
    id_usuario      UUID        NOT NULL,
    id_amigo        UUID        NOT NULL,
    status          INT         NOT NULL,

    CONSTRAINT fk_amigo_id_usuario  FOREIGN KEY(id_usuario)  REFERENCES usuario(id),
    CONSTRAINT fk_amigo_id_amigo    FOREIGN KEY(id_amigo)    REFERENCES usuario(id)
);