package tn.esprit.backendDevops.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.entities.HeuresSup;
import tn.esprit.backendDevops.entities.Tarif;
import tn.esprit.backendDevops.repository.EmployeRepository;
import tn.esprit.backendDevops.repository.HeuresSupRepository;
import tn.esprit.backendDevops.repository.TarifRepository;
import tn.esprit.backendDevops.services.EmployeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private HeuresSupRepository heuresSupRepository;

    @Mock
    private TarifRepository tarifRepository;

    @InjectMocks
    private EmployeService employeService;

    @Test
    void testGetAllEmployes() {
        // Créer une liste d'employés simulée
        List<Employe> employes = Arrays.asList(new Employe(), new Employe());
        // Simuler le comportement du repository pour retourner cette liste
        when(employeRepository.findAll()).thenReturn(employes);

        // Appeler la méthode du service
        List<Employe> result = employeService.getAllEmployes();

        // Vérifier que le résultat n'est pas nul et contient le nombre correct d'employés
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testCreateEmploye() {
        // Créer un employé simulé
        Employe employe = new Employe();
        employe.setNom("Doe");
        employe.setPrenom("John");
        employe.setPoste("Developer");

        // Simuler le comportement du repository pour retourner cet employé
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);

        // Appeler la méthode du service
        Employe result = employeService.createEmploye(employe);

        // Vérifier que le résultat n'est pas nul et que les informations sont correctes
        assertNotNull(result);
        assertEquals("Doe", result.getNom());
    }

    @Test
    void testGetEmployeById() {
        // Créer un employé simulé
        Employe employe = new Employe();
        employe.setId(1L);
        employe.setNom("Doe");
        employe.setPrenom("John");
        employe.setPoste("Developer");

        // Simuler le comportement du repository pour retourner cet employé quand on cherche par ID 1
        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));

        // Appeler la méthode du service
        Optional<Employe> result = employeService.getEmployeById(1L);

        // Vérifier que le résultat est présent et que les informations sont correctes
        //Test pour l'appel du nom de la fonction
        if (result.isPresent()) {
            assertEquals("Doe", result.get().getNom());
        } else {
            fail("Employe not found");
        }
    }

    @Test
    void testCalculateOvertime() {
        // Simuler les données
        int employeId = 1;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        // Créer des enregistrements d'heures supplémentaires simulés
        HeuresSup record1 = new HeuresSup();
        record1.setDate(LocalDate.of(2025, 1, 5));
        record1.setNbHeures(2.0f);

        HeuresSup record2 = new HeuresSup();
        record2.setDate(LocalDate.of(2025, 1, 7));
        record2.setNbHeures(3.0f);

        List<HeuresSup> overtimeRecords = Arrays.asList(record1, record2);

        // Simuler les tarifs
        Tarif weekendTarif = new Tarif();
        weekendTarif.setTypeJour("weekend");
        weekendTarif.setTarif(20.0f);

        Tarif weekdayTarif = new Tarif();
        weekdayTarif.setTypeJour("jour ordinaire");
        weekdayTarif.setTarif(15.0f);

        // Configurer les mocks pour retourner les données simulées
        when(heuresSupRepository.findByEmployeIdAndDateBetween(employeId, startDate, endDate)).thenReturn(overtimeRecords);
        when(tarifRepository.findByTypeJour("weekend")).thenReturn(weekendTarif);
        when(tarifRepository.findByTypeJour("jour ordinaire")).thenReturn(weekdayTarif);

        // Appeler la méthode à tester
        double totalOvertimePay = employeService.calculateOvertime(employeId, startDate, endDate);

        // Vérifier le résultat (2 heures * 20.0 + 3 heures * 15.0 = 40.0 + 45.0 = 85.0)
        assertEquals(85.0, totalOvertimePay, 0.001);
    }
}