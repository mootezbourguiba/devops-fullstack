package tn.esprit.backendDevops.repository;

import tn.esprit.backendDevops.entities.Tarif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import ajouté

@Repository
public interface TarifRepository extends JpaRepository<Tarif, Long> {
    // Méthode pour trouver un tarif par type de jour
    // Amélioration : Retourne Optional<Tarif> pour gérer l'absence de résultat de manière plus sûre
    Optional<Tarif> findByTypeJour(String typeJour);
}