\c postgres;

DROP DATABASE IF EXISTS pret_db;
CREATE DATABASE pret_db;

\c pret_db;

CREATE TABLE contrat_pret (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    montant NUMERIC(10, 2) NOT NULL,
    taux_interet NUMERIC(5, 2) NOT NULL,
    mensualite NUMERIC(10, 2) NOT NULL,
    duree_mois INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL
);

CREATE TABLE mensualite (
    id SERIAL PRIMARY KEY,
    montant_capital NUMERIC(10, 2) NOT NULL,
    montant_interet NUMERIC(10, 2) NOT NULL,
    date_mensualite DATE NOT NULL,
    id_contrat INT NOT NULL,
    FOREIGN KEY (id_contrat) REFERENCES contrat_pret(id) ON DELETE CASCADE
);

CREATE TABLE remboursement (
    id SERIAL PRIMARY KEY,
    id_contrat INT NOT NULL,
    montant NUMERIC(10, 2) NOT NULL,
    date_remboursement DATE NOT NULL,
    FOREIGN KEY (id_contrat) REFERENCES contrat_pret(id) ON DELETE CASCADE
);