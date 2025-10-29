\c postgres;

DROP DATABASE IF EXISTS central_db;
CREATE DATABASE central_db;

\c central_db;

CREATE TABLE action_role (
    id SERIAL PRIMARY KEY,
    nom_table VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    role_lvl INT NOT NULL
);

INSERT INTO action_role (nom_table, action, role_lvl) VALUES
('transaction_courant', 'insert', 5),
('transaction_courant', 'insert', 10),
('transaction_courant', 'update', 10),
('transaction_courant', 'delete', 10),
('compte_courant', 'view', 5),
('compte_courant', 'view', 10);

CREATE TABLE directions (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT
);