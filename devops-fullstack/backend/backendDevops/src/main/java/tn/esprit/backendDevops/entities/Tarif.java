package tn.esprit.backendDevops.entities; // Ton package

import javax.persistence.Column; // Import pour @Column
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor; // Import pour @AllArgsConstructor
import lombok.Data;
import lombok.NoArgsConstructor; // Import pour @NoArgsConstructor

@Entity
@Table(name = "tarif") // Garde le nom explicite de la table
@Data
@NoArgsConstructor // Ajouté : Constructeur sans arguments (requis par JPA)
@AllArgsConstructor // Ajouté : Constructeur avec tous les arguments (pratique)
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Rend le nom de colonne et les contraintes explicites
    @Column(name = "type_jour", nullable = false, length = 50)
    private String typeJour;  // "weekend" or "jour ordinaire"

    // Rend la contrainte explicite
    @Column(nullable = false)
    private float tarif;
}