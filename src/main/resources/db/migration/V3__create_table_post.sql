CREATE TABLE IF NOT EXISTS post (
    id                UUID PRIMARY KEY NOT NULL,
    id_usuario        UUID NOT NULL,
    data_criacao      TIMESTAMP NOT NULL,
    descricao         TEXT NOT NULL
);