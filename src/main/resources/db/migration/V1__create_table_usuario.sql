CREATE TABLE IF NOT EXISTS usuario (
    id          UUID PRIMARY KEY NOT NULL,
    nome        VARCHAR(50) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    senha       VARCHAR(255) NOT NULL
);