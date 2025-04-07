package tn.esprit.backendDevops.entities;

import javax.persistence.Column; // Ajouté
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor; // Ajouté
import lombok.Data;
import lombok.NoArgsConstructor; // Ajouté

@Entity
@Table(name = "employe")
@Data
@NoArgsConstructor // Ajouté
@AllArgsConstructor // Ajouté
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Ajouté (supposant que le nom est requis)
    private String nom;

    @Column(nullable = false) // Ajouté (supposant que le prénom est requis)
    private String prenom;

    @Column(nullable = true) // Ajouté (le poste pourrait être facultatif ?)
    private String poste;
}