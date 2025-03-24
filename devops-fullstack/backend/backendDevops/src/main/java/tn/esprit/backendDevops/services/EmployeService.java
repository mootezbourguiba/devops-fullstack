package tn.esprit.backendDevops.services;// EmployeService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.entities.HeuresSup;
import tn.esprit.backendDevops.entities.Tarif;
import tn.esprit.backendDevops.repository.EmployeRepository;
import tn.esprit.backendDevops.repository.HeuresSupRepository;
import tn.esprit.backendDevops.repository.TarifRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private HeuresSupRepository heuresSupRepository;

    @Autowired
    private TarifRepository tarifRepository;

    public List<Employe> getAllEmployes() {
        return employeRepository.findAll();
    }
    public Employe createEmploye(Employe employe) {
        return employeRepository.save(employe);
    }

    public Optional<Employe> getEmployeById(Long id) {
        return employeRepository.findById(id);
    }

    public double calculateOvertime(int employeId, LocalDate startDate, LocalDate endDate) {
        double totalOvertimePay = 0.0;

        // 1. Récupérer tous les enregistrements d'heures supplémentaires pour l'employé
        List<HeuresSup> overtimeRecords = heuresSupRepository.findByEmployeIdAndDateBetween(employeId, startDate, endDate);

        // 2. Itérer sur les enregistrements et calculer les heures supplémentaires
        for (HeuresSup record : overtimeRecords) {
            Date recordDate = record.getDate();
            DayOfWeek dayOfWeek = DayOfWeek.of(recordDate.getDay());
            Tarif tarif;

            // Déterminer si c'est un jour de week-end ou un jour de semaine
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                // Supposons qu'il n'y a qu'un seul tarif week-end, sinon vous devez récupérer le bon
                tarif = tarifRepository.findByTypeJour("weekend");

            } else {
                // Supposons qu'il n'y a qu'un seul tarif de jour de semaine, sinon vous devez récupérer le bon
                tarif = tarifRepository.findByTypeJour("jour ordinaire");
            }

            if (tarif != null) {
                totalOvertimePay += record.getNbHeures() * tarif.getTarif();
            }
        }

        return totalOvertimePay;
    }
}