-- Insertion des tarifs initiaux indispensables
-- Assure-toi que les 'type_jour' correspondent EXACTEMENT à ceux utilisés dans EmployeService
INSERT INTO tarif (type_jour, tarif) VALUES
('jour ordinaire', 1.25),  -- Exemple: Heures normales majorées de 25%
('weekend', 1.5);          -- Exemple: Heures week-end majorées de 50%

-- Insertion de quelques employés pour les tests/démonstration
INSERT INTO employe (nom, prenom, poste) VALUES
('Bourguiba', 'Mootez', 'Développeur Fullstack'),
('Ben Foulen', 'Foulen', 'Ingénieur DevOps'),
('Test', 'User', 'Stagiaire');

-- Insertion de quelques heures supplémentaires pour les tests/démonstration
-- Note: Les IDs des employés dépendent de l'ordre d'insertion ci-dessus (1, 2, 3...)
--       Assure-toi que les dates sont dans un format que MySQL comprend (YYYY-MM-DD)

-- Heures pour Mootez Bourguiba (ID=1)
INSERT INTO heures_sup (employe_id, date, nb_heures) VALUES
(1, '2024-04-01', 2.0),  -- Lundi (jour ordinaire)
(1, '2024-04-06', 3.5),  -- Samedi (weekend)
(1, '2024-04-07', 4.0);  -- Dimanche (weekend)

-- Heures pour Foulen Ben Foulen (ID=2)
INSERT INTO heures_sup (employe_id, date, nb_heures) VALUES
(2, '2024-04-03', 1.5);  -- Mercredi (jour ordinaire)