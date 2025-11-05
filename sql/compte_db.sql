\c postgres;

DROP DATABASE IF EXISTS compte_db;
CREATE DATABASE compte_db;

\c compte_db;

-- CREATE TABLE type_compte (
--     id SERIAL PRIMARY KEY,
--     nom VARCHAR(20)
-- );

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

CREATE TABLE contexte_transaction (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO contexte_transaction (libelle) VALUES 
('STANDARD'),
('VIREMENT');

CREATE TABLE transaction_courant (
    id SERIAL PRIMARY KEY,
    id_compte INT REFERENCES compte_courant(id),
    montant DECIMAL(15, 2) NOT NULL,
    type_transaction INT REFERENCES type_transaction(id),
    id_contexte_transaction INT REFERENCES contexte_transaction(id),
    devise_ref INT,
    id_virement_source INT REFERENCES virement(id),
    date_transaction DATE DEFAULT CURRENT_DATE
);

CREATE TABLE frequence_plafond (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO frequence_plafond (libelle) VALUES 
('MENSUEL'),
('OPERATION'),
('JOURNALIER');

CREATE TABLE plafond (
    id SERIAL PRIMARY KEY,
    montant DECIMAL(15, 2) NOT NULL,
    id_type_transaction INT REFERENCES type_transaction(id),
    id_frequence_plafond INT REFERENCES frequence_plafond(id),
    id_contexte_transaction INT REFERENCES contexte_transaction(id),
    id_compte INT REFERENCES compte_courant(id),
    date_debut DATE NOT NULL,
    date_fin DATE
);

CREATE TABLE virement (
    id SERIAL PRIMARY KEY,
    id_compte INT REFERENCES compte_courant(id) NOT NULL,
    id_compte_beneficiaire INT REFERENCES compte_courant(id) NOT NULL ,
    montant DECIMAL(15, 2) NOT NULL,
    date_virement DATE DEFAULT CURRENT_DATE NOT NULL,
    devise_ref INT
    -- id_transaction_entree INT REFERENCES transaction_courant(id) NOT NULL,
    -- id_transaction_sortie INT REFERENCES transaction_courant(id) NOT NULL
);

CREATE TABLE libelle_statut_generique (
    id SERIAL PRIMARY KEY,
    table_reference VARCHAR(50) NOT NULL,
    libelle VARCHAR(30) NOT NULL
);

CREATE TABLE statut_generique (
    id SERIAL PRIMARY KEY,
    table_reference VARCHAR(50) NOT NULL,
    id_reference INT NOT NULL,
    id_libelle INT REFERENCES libelle_statut_generique(id) NOT NULL,
    id_utilisateur INT REFERENCES utilisateurs(id),
    date_statut DATE DEFAULT CURRENT_DATE NOT NULL
);

CREATE TABLE frais_bancaire (
    id SERIAL PRIMARY KEY,
    montant_inf DECIMAL(15, 2),
    montant_sup DECIMAL(15, 2),
    frais_forfaitaire DECIMAL, 
    frais_pourcentage  DECIMAL,
    date_frais DATE DEFAULT CURRENT_DATE NOT NULL
);

CREATE TABLE etat_virement (
    id SERIAL PRIMARY KEY,
    id_virement INT REFERENCES virement(id),
    etat INT,
    date_etat TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

INSERT INTO libelle_statut_generique (table_reference, libelle) VALUES 
('transaction_courant', 'VALIDE'),
('transaction_courant', 'ANNULE'),
('transaction_courant', 'EN_ATTENTE'),
('virement', 'VALIDE'),
('virement', 'ANNULE');
('virement', 'EN_ATTENTE');

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
(2, 4000.75);

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