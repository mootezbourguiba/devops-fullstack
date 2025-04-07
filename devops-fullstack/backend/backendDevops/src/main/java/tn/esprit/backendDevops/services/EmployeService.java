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

    // Import nécessaire pour RuntimeException (si tu utilises une exception spécifique, importe-la)
    // import java.lang.RuntimeException;
    // Import nécessaire pour DayOfWeek si ce n'est pas déjà fait
    // import java.time.DayOfWeek;

    public double calculateOvertime(Long employeId, LocalDate startDate, LocalDate endDate) { // Changé int en Long pour employeId
        double totalOvertimePay = 0.0;

        // 1. Récupérer tous les enregistrements d'heures supplémentaires pour l'employé
        // Utilise directement employeId (Long)
        List<HeuresSup> overtimeRecords = heuresSupRepository.findByEmployeIdAndDateBetween(employeId, startDate, endDate);

        // 2. Itérer sur les enregistrements et calculer les heures supplémentaires
        for (HeuresSup record : overtimeRecords) {
            LocalDate recordDate = record.getDate();
            DayOfWeek dayOfWeek = recordDate.getDayOfWeek();
            String typeJour;

            // Déterminer le type de jour
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                typeJour = "weekend";
            } else {
                typeJour = "jour ordinaire";
            }

            // Récupérer le tarif correspondant. S'il n'est pas trouvé, lance une exception.
            Tarif tarifApplicable = tarifRepository.findByTypeJour(typeJour)
                    .orElseThrow(() -> new RuntimeException("Configuration Error: Tarif non trouvé pour le type : " + typeJour));

            // Maintenant, nous sommes sûrs que tarifApplicable est un objet Tarif valide (non Optional)
            // et non null. On peut accéder à son tarif.
            totalOvertimePay += record.getNbHeures() * tarifApplicable.getTarif();
        }

        return totalOvertimePay;
    }
}