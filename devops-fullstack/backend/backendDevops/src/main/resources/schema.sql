CREATE TABLE Employe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    poste VARCHAR(255)
);

CREATE TABLE Heures_Sup (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employe_id INT,
    date DATE,
    nb_heures FLOAT,
    FOREIGN KEY (employe_id) REFERENCES Employe(id)
);

CREATE TABLE Tarif (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_jour VARCHAR(255),
    tarif FLOAT
);