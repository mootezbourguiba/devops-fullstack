package tn.esprit.backendDevops.repository;



import tn.esprit.backendDevops.entities.HeuresSup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeuresSupRepository extends JpaRepository<HeuresSup, Long> {
}