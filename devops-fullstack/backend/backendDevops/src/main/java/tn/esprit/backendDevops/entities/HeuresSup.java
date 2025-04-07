package tn.esprit.backendDevops.entities; // Ton package

// Imports nécessaires (javax.persistence)
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType; // Import pour FetchType
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
// Suppression de @Temporal et java.util.Date car non nécessaires pour LocalDate

import lombok.AllArgsConstructor; // Import pour @AllArgsConstructor
import lombok.Data;
import lombok.NoArgsConstructor; // Import pour @NoArgsConstructor

import java.time.LocalDate;
// Assure-toi que Employe est aussi dans ce package ou importe-le correctement
// import tn.esprit.backendDevops.entities.Employe;

@Entity
@Table(name = "heures_sup") // Garde le nom explicite de la table
@Data
@NoArgsConstructor // Ajouté : Constructeur sans arguments
@AllArgsConstructor // Ajouté : Constructeur avec tous les arguments
public class HeuresSup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ajout de fetch = FetchType.LAZY pour la performance
    @ManyToOne(fetch = FetchType.LAZY) // <- Ajouté
    @JoinColumn(name = "employe_id", nullable = false) // Garde le nom explicite de la colonne FK
    private Employe employe; // Assure-toi que la classe Employe existe et est importée

    // Rend la contrainte explicite
    @Column(nullable = false)
    private LocalDate date;

    // Rend le nom de colonne et la contrainte explicites
    @Column(name = "nb_heures", nullable = false)
    private float nbHeures;
}