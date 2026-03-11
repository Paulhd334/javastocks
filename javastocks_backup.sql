-- =====================================================
-- SCRIPT DE CREATION COMPLET - JavaStocks Database
-- PostgreSQL 17
-- =====================================================

-- Supprimer les tables si elles existent (ordre inverse des dépendances)
DROP TABLE IF EXISTS demande_article CASCADE;
DROP TABLE IF EXISTS demande_reappro CASCADE;
DROP TABLE IF EXISTS reservation_article CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS article CASCADE;
DROP TABLE IF EXISTS fournisseur CASCADE;
DROP TABLE IF EXISTS point_livraison CASCADE;
DROP TABLE IF EXISTS type_epreuve CASCADE;
DROP TABLE IF EXISTS coureur CASCADE;

-- =====================================================
-- 1. TABLES DE BASE
-- =====================================================

-- Table des coureurs
CREATE TABLE coureur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL
);

-- Table des types d'épreuve
CREATE TABLE type_epreuve (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Table des fournisseurs
CREATE TABLE fournisseur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    rue VARCHAR(200),
    cp VARCHAR(10),
    ville VARCHAR(100),
    tel VARCHAR(20),
    email VARCHAR(100)
);

-- Table des points de livraison
CREATE TABLE point_livraison (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    rue VARCHAR(200),
    cp VARCHAR(10),
    ville VARCHAR(100),
    tel VARCHAR(20),
    email VARCHAR(100)
);

-- Table des articles
CREATE TABLE article (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(200) NOT NULL,
    quantite INTEGER NOT NULL DEFAULT 0,
    indicateur_sl BOOLEAN DEFAULT FALSE,
    categorie VARCHAR(20) NOT NULL,
    seuil_reappro INTEGER DEFAULT 0,
    taille VARCHAR(10),
    couleur VARCHAR(50),
    volume INTEGER,
    poids INTEGER
);

-- =====================================================
-- 2. TABLES DE RESERVATION
-- =====================================================

-- Table des réservations
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    date_reservation DATE NOT NULL,
    coureur_id INTEGER REFERENCES coureur(id),
    type_epreuve_id INTEGER REFERENCES type_epreuve(id),
    statut VARCHAR(20) DEFAULT 'en_attente'
);

-- Table de liaison réservation-article
CREATE TABLE reservation_article (
    reservation_id INTEGER REFERENCES reservation(id),
    article_id INTEGER REFERENCES article(id),
    quantite INTEGER NOT NULL,
    PRIMARY KEY (reservation_id, article_id)
);

-- =====================================================
-- 3. TABLES DE REAPPROVISIONNEMENT
-- =====================================================

-- Table des demandes de réappro
CREATE TABLE demande_reappro (
    id SERIAL PRIMARY KEY,
    motif VARCHAR(10) NOT NULL,
    date_demande DATE NOT NULL DEFAULT CURRENT_DATE,
    numero_commande VARCHAR(50),
    fournisseur_id INTEGER REFERENCES fournisseur(id),
    point_livraison_id INTEGER REFERENCES point_livraison(id)
);

-- Table de liaison demande-article
CREATE TABLE demande_article (
    demande_id INTEGER REFERENCES demande_reappro(id),
    article_id INTEGER REFERENCES article(id),
    quantite INTEGER NOT NULL,
    PRIMARY KEY (demande_id, article_id)
);

-- =====================================================
-- 4. INSERTION DES DONNEES DE TEST
-- =====================================================

-- 4.1 Coureurs
INSERT INTO coureur (nom, prenom) VALUES
('Pérec', 'Marie-José'),
('Diack', 'Amadou'),
('Parker', 'Tony'),
('Noah', 'Yannick'),
('Mauresmo', 'Amélie'),
('Zidane', 'Zinédine'),
('Riner', 'Teddy'),
('Lavillenie', 'Renaud'),
('Mayer', 'Kevin'),
('Vollmer', 'Sarah'),
('Agbegnenou', 'Clarisse'),
('Manaudou', 'Florent'),
('Muffat', 'Camille'),
('Lemaitre', 'Christophe'),
('Benzema', 'Karim'),
('Mbappé', 'Kylian'),
('Griezmann', 'Antoine'),
('Henry', 'Thierry'),
('Platini', 'Michel'),
('Papin', 'Jean-Pierre');

-- 4.2 Types d'épreuve
INSERT INTO type_epreuve (libelle) VALUES
('Marathon'),
('Semi-marathon'),
('10 km'),
('5 km'),
('Trail'),
('Course de montagne'),
('Triathlon'),
('Ironman'),
('Duathlon'),
('Course orientation'),
('Randonnée'),
('Sprint'),
('Cross-country'),
('Ultra-trail'),
('Course relais');

-- 4.3 Fournisseurs
INSERT INTO fournisseur (nom, rue, cp, ville, tel, email) VALUES
('Decathlon Pro', '15 Rue du Général de Gaulle', '59650', 'Villeneuve d''Ascq', '03.20.45.67.89', 'pro@decathlon.fr'),
('Metro Cash & Carry', '45 Rue de la Gare', '93400', 'Saint-Ouen', '01.48.13.25.36', 'contact@metro.fr'),
('Transgourmet', '12 Avenue du Maréchal Juin', '69200', 'Vénissieux', '04.72.89.01.23', 'commande@transgourmet.fr'),
('Daucy', '8 Rue des Conserves', '56100', 'Lorient', '02.97.64.12.78', 'pro@daucy.fr'),
('Pasquier', '25 Route de Paris', '49100', 'Angers', '02.41.34.56.78', 'commercial@pasquier.fr'),
('Brasseurs de France', '17 Rue de la Brasserie', '67000', 'Strasbourg', '03.88.45.67.89', 'contact@brasseursdefrance.fr');

-- 4.4 Points de livraison
INSERT INTO point_livraison (nom, rue, cp, ville, tel, email) VALUES
('Entrepot Nord', '45 Rue de la Chaine du Froid', '59140', 'Dunkerque', '03.28.63.12.45', 'entrepot.nord@webcourses.fr'),
('Entrepot Sud', '12 Avenue de la Fraicheur', '13008', 'Marseille', '04.91.34.56.78', 'entrepot.sud@webcourses.fr'),
('Entrepot Est', '28 Rue des Saisons', '67000', 'Strasbourg', '03.88.12.34.56', 'entrepot.est@webcourses.fr'),
('Entrepot Ouest', '5 Boulevard de l Ocean', '44000', 'Nantes', '02.40.45.67.89', 'entrepot.ouest@webcourses.fr'),
('Depot Central', '17 Rue de la Logistique', '75012', 'Paris', '01.43.45.67.89', 'depot.central@webcourses.fr'),
('Plateforme Bio', '33 Chemin des Producteurs', '84000', 'Avignon', '04.90.12.34.56', 'bio@webcourses.fr');

-- 4.5 Articles (avec catégories)
-- Boissons
INSERT INTO article (libelle, quantite, indicateur_sl, categorie, volume, seuil_reappro) VALUES
('Coca-Cola 33cl', 150, false, 'Boisson', 33, 50),
('Coca-Cola 50cl', 200, false, 'Boisson', 50, 60),
('Coca-Cola 1.5L', 80, false, 'Boisson', 150, 30),
('Evian 50cl', 300, false, 'Boisson', 50, 100),
('Evian 1L', 150, false, 'Boisson', 100, 50),
('Heineken 33cl', 200, false, 'Boisson', 33, 80),
('Sprite 33cl', 120, false, 'Boisson', 33, 40),
('Oasis Tropical', 90, false, 'Boisson', 33, 40),
('Perrier 33cl', 90, false, 'Boisson', 33, 30),
('Red Bull 25cl', 60, false, 'Boisson', 25, 30);

-- Textiles
INSERT INTO article (libelle, quantite, indicateur_sl, categorie, taille, couleur, seuil_reappro) VALUES
('T-shirt Blanc', 500, false, 'Textile', 'M', 'Blanc', 100),
('T-shirt Blanc', 450, false, 'Textile', 'L', 'Blanc', 90),
('T-shirt Blanc', 300, false, 'Textile', 'XL', 'Blanc', 60),
('T-shirt Noir', 400, false, 'Textile', 'M', 'Noir', 80),
('T-shirt Noir', 350, false, 'Textile', 'L', 'Noir', 70),
('Short Sport Noir', 150, false, 'Textile', 'M', 'Noir', 40),
('Short Sport Noir', 120, false, 'Textile', 'L', 'Noir', 30),
('Short Sport Bleu', 100, false, 'Textile', 'M', 'Bleu', 25),
('Veste Imperméable', 50, false, 'Textile', 'L', 'Vert', 15),
('Casquette', 300, false, 'Textile', 'Unique', 'Noir', 80);

-- Denrées
INSERT INTO article (libelle, quantite, indicateur_sl, categorie, poids, seuil_reappro) VALUES
('Pâtes Spaghetti 500g', 200, false, 'Denree', 500, 50),
('Pâtes Coquillettes 500g', 180, false, 'Denree', 500, 45),
('Riz Basmati 1kg', 150, false, 'Denree', 1000, 40),
('Riz Thaï 500g', 120, false, 'Denree', 500, 35),
('Farine de Blé 1kg', 100, false, 'Denree', 1000, 30),
('Sucre en Poudre 1kg', 90, false, 'Denree', 1000, 25),
('Lentilles Vertes 500g', 80, false, 'Denree', 500, 20),
('Pois Cassés 500g', 70, false, 'Denree', 500, 20),
('Couscous 1kg', 60, false, 'Denree', 1000, 15),
('Semoule Fine 500g', 55, false, 'Denree', 500, 15);

-- =====================================================
-- 5. RESERVATIONS DE TEST
-- =====================================================

-- Réservation 1 : Zidane (Marathon)
WITH r1 AS (
    INSERT INTO reservation (date_reservation, coureur_id, type_epreuve_id, statut)
    VALUES ('2026-03-15', 
            (SELECT id FROM coureur WHERE nom = 'Zidane'), 
            (SELECT id FROM type_epreuve WHERE libelle = 'Marathon'),
            'validee')
    RETURNING id
)
INSERT INTO reservation_article (reservation_id, article_id, quantite)
SELECT r1.id, a.id, 
       CASE 
           WHEN a.libelle = 'Coca-Cola 33cl' THEN 3
           WHEN a.libelle = 'T-shirt Blanc' AND a.taille = 'L' THEN 1
           WHEN a.libelle = 'Pâtes Spaghetti 500g' THEN 5
           ELSE 0
       END
FROM r1, article a
WHERE (a.libelle = 'Coca-Cola 33cl')
   OR (a.libelle = 'T-shirt Blanc' AND a.taille = 'L')
   OR (a.libelle = 'Pâtes Spaghetti 500g');

-- Réservation 2 : Mbappé (10 km)
WITH r2 AS (
    INSERT INTO reservation (date_reservation, coureur_id, type_epreuve_id, statut)
    VALUES ('2026-03-20', 
            (SELECT id FROM coureur WHERE nom = 'Mbappé'), 
            (SELECT id FROM type_epreuve WHERE libelle = '10 km'),
            'validee')
    RETURNING id
)
INSERT INTO reservation_article (reservation_id, article_id, quantite)
SELECT r2.id, a.id, 
       CASE 
           WHEN a.libelle = 'Evian 50cl' THEN 2
           WHEN a.libelle = 'Short Sport Noir' AND a.taille = 'M' THEN 1
           WHEN a.libelle = 'Riz Basmati 1kg' THEN 3
           ELSE 0
       END
FROM r2, article a
WHERE (a.libelle = 'Evian 50cl')
   OR (a.libelle = 'Short Sport Noir' AND a.taille = 'M')
   OR (a.libelle = 'Riz Basmati 1kg');

-- Réservation 3 : Riner (Trail) - EN ATTENTE
WITH r3 AS (
    INSERT INTO reservation (date_reservation, coureur_id, type_epreuve_id, statut)
    VALUES ('2026-03-18', 
            (SELECT id FROM coureur WHERE nom = 'Riner'), 
            (SELECT id FROM type_epreuve WHERE libelle = 'Trail'),
            'en_attente')
    RETURNING id
)
INSERT INTO reservation_article (reservation_id, article_id, quantite)
SELECT r3.id, a.id, 
       CASE 
           WHEN a.libelle = 'Coca-Cola 33cl' THEN 10  -- Demande excessive
           WHEN a.libelle = 'Veste Imperméable' THEN 2
           WHEN a.libelle = 'Pâtes Spaghetti 500g' THEN 8
           ELSE 0
       END
FROM r3, article a
WHERE (a.libelle = 'Coca-Cola 33cl')
   OR (a.libelle = 'Veste Imperméable')
   OR (a.libelle = 'Pâtes Spaghetti 500g');

-- =====================================================
-- 6. DEMANDES DE REAPPROVISIONNEMENT
-- =====================================================

-- Demande 1 : Métro (boissons)
WITH d1 AS (
    INSERT INTO demande_reappro (motif, date_demande, numero_commande, fournisseur_id, point_livraison_id)
    VALUES ('R', '2026-02-25', 'CMD-20250225-001',
            (SELECT id FROM fournisseur WHERE nom = 'Metro Cash & Carry'),
            (SELECT id FROM point_livraison WHERE nom = 'Entrepot Nord'))
    RETURNING id
)
INSERT INTO demande_article (demande_id, article_id, quantite)
SELECT d1.id, a.id, 
       CASE 
           WHEN a.libelle = 'Coca-Cola 33cl' THEN 50
           WHEN a.libelle = 'Evian 50cl' THEN 30
           WHEN a.libelle = 'Heineken 33cl' THEN 25
           ELSE 0
       END
FROM d1, article a
WHERE a.libelle IN ('Coca-Cola 33cl', 'Evian 50cl', 'Heineken 33cl');

-- Demande 2 : Transgourmet (denrées)
WITH d2 AS (
    INSERT INTO demande_reappro (motif, date_demande, numero_commande, fournisseur_id, point_livraison_id)
    VALUES ('R', '2026-02-24', 'CMD-20250224-015',
            (SELECT id FROM fournisseur WHERE nom = 'Transgourmet'),
            (SELECT id FROM point_livraison WHERE nom = 'Entrepot Sud'))
    RETURNING id
)
INSERT INTO demande_article (demande_id, article_id, quantite)
SELECT d2.id, a.id, 
       CASE 
           WHEN a.libelle = 'Pâtes Spaghetti 500g' THEN 100
           WHEN a.libelle = 'Riz Basmati 1kg' THEN 50
           WHEN a.libelle = 'Farine de Blé 1kg' THEN 30
           ELSE 0
       END
FROM d2, article a
WHERE a.libelle IN ('Pâtes Spaghetti 500g', 'Riz Basmati 1kg', 'Farine de Blé 1kg');

-- =====================================================
-- 7. INDEX POUR OPTIMISATION
-- =====================================================

CREATE INDEX idx_article_categorie ON article(categorie);
CREATE INDEX idx_article_indicateur ON article(indicateur_sl);
CREATE INDEX idx_reservation_date ON reservation(date_reservation);
CREATE INDEX idx_reservation_statut ON reservation(statut);
CREATE INDEX idx_reservation_coureur ON reservation(coureur_id);
CREATE INDEX idx_demande_reappro_date ON demande_reappro(date_demande);

-- =====================================================
-- 8. VUES UTILES
-- =====================================================

-- Vue des articles en rupture
CREATE OR REPLACE VIEW v_articles_rupture AS
SELECT id, libelle, quantite, seuil_reappro, categorie
FROM article
WHERE quantite < seuil_reappro AND indicateur_sl = FALSE;

-- Vue des réservations en attente
CREATE OR REPLACE VIEW v_reservations_attente AS
SELECT r.id, r.date_reservation, 
       c.nom || ' ' || c.prenom as coureur,
       te.libelle as epreuve,
       COUNT(ra.article_id) as nb_articles,
       SUM(ra.quantite) as total_articles
FROM reservation r
JOIN coureur c ON r.coureur_id = c.id
JOIN type_epreuve te ON r.type_epreuve_id = te.id
JOIN reservation_article ra ON r.id = ra.reservation_id
WHERE r.statut = 'en_attente'
GROUP BY r.id, c.nom, c.prenom, te.libelle;

-- =====================================================
-- 9. VERIFICATIONS FINALES
-- =====================================================

-- Compter les enregistrements
SELECT 'coureur' as table_name, COUNT(*) as nb FROM coureur UNION ALL
SELECT 'type_epreuve', COUNT(*) FROM type_epreuve UNION ALL
SELECT 'fournisseur', COUNT(*) FROM fournisseur UNION ALL
SELECT 'point_livraison', COUNT(*) FROM point_livraison UNION ALL
SELECT 'article', COUNT(*) FROM article UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation UNION ALL
SELECT 'demande_reappro', COUNT(*) FROM demande_reappro;