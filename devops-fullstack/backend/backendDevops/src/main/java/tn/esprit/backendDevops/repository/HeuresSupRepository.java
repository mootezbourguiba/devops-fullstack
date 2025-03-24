package tn.esprit.backendDevops.repository;



import tn.esprit.backendDevops.entities.HeuresSup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HeuresSupRepository extends JpaRepository<HeuresSup, Long> {
    // Méthode pour trouver les heures supplémentaires par employé et par plage de dates
    List<HeuresSup> findByEmployeIdAndDateBetween(int employeId, LocalDate startDate, LocalDate endDate);
}