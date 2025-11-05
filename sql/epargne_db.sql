\c postgres;

DROP DATABASE IF EXISTS epargne_db;
CREATE DATABASE epargne_db;

\c depot_db;

CREATE TABLE compte_epargne (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    solde_initial DECIMAL(15, 2) DEFAULT 0.00,
    taux_interet DECIMAL(5, 2) NOT NULL,
    date_creation DATE DEFAULT CURRENT_DATE
);

CREATE TABLE type_transaction (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE transaction_epargne (
    id SERIAL PRIMARY KEY,
    id_compte INT REFERENCES compte_epargne(id),
    montant DECIMAL(15, 2) NOT NULL,
    type_transaction INT REFERENCES type_transaction(id),
    date_transaction DATE DEFAULT CURRENT_DATE
);