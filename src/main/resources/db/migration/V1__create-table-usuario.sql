CREATE TABLE IF NOT EXISTS usuarios (
    usuario_id  SERIAL PRIMARY KEY NOT NULL,
    nome        VARCHAR(50) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    senha       VARCHAR(255) NOT NULL
);