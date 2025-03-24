package tn.esprit.backendDevops.repository;

import tn.esprit.backendDevops.entities.Tarif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarifRepository extends JpaRepository<Tarif, Long> {
    // Méthode pour trouver un tarif par type de jour
    Tarif findByTypeJour(String typeJour);
}