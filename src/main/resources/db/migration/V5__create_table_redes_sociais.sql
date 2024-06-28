CREATE TABLE IF NOT EXISTS rede_social(
     id             UUID            PRIMARY KEY NOT NULL,
     id_usuario     UUID            NOT NULL,
     nome           VARCHAR(50)     NOT NULL,
     link           VARCHAR(100)    NOT NULL,

    CONSTRAINT fk_rede_social_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id)
)