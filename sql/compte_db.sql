\c postgres;

DROP DATABASE IF EXISTS compte_db;
CREATE DATABASE compte_db;

\c compte_db;

CREATE TABLE utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    id_direction INT NOT NULL,
    role_lvl INT NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO utilisateurs (nom, id_direction, role_lvl, mot_de_passe) VALUES
('admin', 1, 10, 'a'),
('user', 2, 5, 'a');

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(15),
    date_creation DATE DEFAULT CURRENT_DATE
);

CREATE TABLE compte_courant (
    id SERIAL PRIMARY KEY,
    id_client INT REFERENCES clients(id),
    solde_initial DECIMAL(15, 2) DEFAULT 0.00,
    date_creation DATE DEFAULT CURRENT_DATE
);

CREATE TABLE type_transaction (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE transaction_courant (
    id SERIAL PRIMARY KEY,
    id_compte INT REFERENCES compte_courant(id),
    montant DECIMAL(15, 2) NOT NULL,
    type_transaction INT REFERENCES type_transaction(id),
    date_transaction DATE DEFAULT CURRENT_DATE
);

-- 1. Insertion des Types de Transaction
-- Nécessaire pour les références dans la table transaction_courant
INSERT INTO type_transaction (nom, description) VALUES
('DEBIT', 'Retrait d''argent ou paiement.'),
('CREDIT', 'Dépôt d''argent ou virement reçu.');

---

-- 2. Insertion de DEUX Clients (Données de Test)
INSERT INTO clients (nom, prenom, email, telephone) VALUES
('Dupont', 'Alice', 'alice.dupont@email.com', '0123456789'),
('Martin', 'Bob', 'bob.martin@email.com', '0987654321');

---

-- 3. Insertion de DEUX Comptes Courants
-- Associer les comptes aux clients que nous venons de créer.
-- Nous supposons que l'ID d'Alice est 1 et l'ID de Bob est 2 (si ce sont les premières insertions).

-- Compte pour Alice Dupont (ID 1)
INSERT INTO compte_courant (id_client, solde_initial) VALUES
(1, 1500.50);

-- Compte pour Bob Martin (ID 2)
INSERT INTO compte_courant (id_client, solde_initial) VALUES
(4000.75);

---

-- 4. Insertion de Transactions Initiales (Facultatif mais utile pour simuler l'activité)
-- Utiliser les types de transaction (DEBIT=1, CREDIT=2, si ce sont les premières insertions)
-- Et les ID de compte (Alice=1, Bob=2)

-- Transaction sur le compte d'Alice (ID 1)
INSERT INTO transaction_courant (id_compte, montant, type_transaction) VALUES
(1, 200.00, 2); -- Crédit de 200.00

-- Transaction sur le compte de Bob (ID 2)
INSERT INTO transaction_courant (id_compte, montant, type_transaction) VALUES
(2, 50.00, 1); -- Débit de 50.00