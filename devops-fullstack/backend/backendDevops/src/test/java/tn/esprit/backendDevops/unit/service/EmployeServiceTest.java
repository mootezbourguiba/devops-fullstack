package tn.esprit.backendDevops.unit.service; // Garde ton package

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

// import java.time.DayOfWeek; // Pas utilisé directement ici
import java.time.LocalDate;
// import java.time.ZoneId; // Pas utilisé
// import java.util.Date; // Pas utilisé
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

    // --- Les tests pour getAllEmployes, createEmploye, getEmployeById restent inchangés ---
    // (Tu les avais déjà écrits correctement avec Long pour l'ID dans getEmployeById)

    @Test
    void testGetAllEmployes() {
        List<Employe> employes = Arrays.asList(new Employe(), new Employe());
        when(employeRepository.findAll()).thenReturn(employes);
        List<Employe> result = employeService.getAllEmployes();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testCreateEmploye() {
        Employe employe = new Employe();
        employe.setNom("Doe");
        employe.setPrenom("John");
        employe.setPoste("Developer");
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);
        Employe result = employeService.createEmploye(employe);
        assertNotNull(result);
        assertEquals("Doe", result.getNom());
    }

    @Test
    void testGetEmployeById() {
        Employe employe = new Employe();
        employe.setId(1L);
        employe.setNom("Doe");
        employe.setPrenom("John");
        employe.setPoste("Developer");
        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));
        Optional<Employe> result = employeService.getEmployeById(1L);
        assertTrue(result.isPresent(), "Employe should be found"); // Meilleure assertion pour Optional
        assertEquals("Doe", result.get().getNom());
    }


    // --- Test corrigé pour calculateOvertime ---
    @Test
    void testCalculateOvertime() {
        // Simuler les données
        Long employeId = 1L; // *** CORRECTION: Utilise Long ***
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        // Créer des enregistrements d'heures supplémentaires simulés
        // IMPORTANT: Assure-toi d'associer un Employe mock ou réel si ta logique en a besoin
        // Pour ce test spécifique du calcul, on n'a pas besoin de l'Employe lui-même,
        // juste les heures et la date.
        HeuresSup record1 = new HeuresSup(101L, null, LocalDate.of(2025, 1, 5), 2.0f); // Dimanche -> Weekend
        HeuresSup record2 = new HeuresSup(102L, null, LocalDate.of(2025, 1, 7), 3.0f); // Mardi -> Jour ordinaire

        List<HeuresSup> overtimeRecords = Arrays.asList(record1, record2);

        // Simuler les tarifs
        Tarif weekendTarif = new Tarif(1L, "weekend", 20.0f); // ID ajouté pour clarté
        Tarif weekdayTarif = new Tarif(2L, "jour ordinaire", 15.0f); // ID ajouté pour clarté

        // Configurer les mocks pour retourner les données simulées
        // *** CORRECTION: Utilise employeId (Long) ***
        when(heuresSupRepository.findByEmployeIdAndDateBetween(employeId, startDate, endDate))
                .thenReturn(overtimeRecords);

        // *** CORRECTION: Mock pour retourner Optional<Tarif> ***
        when(tarifRepository.findByTypeJour("weekend"))
                .thenReturn(Optional.of(weekendTarif)); // Enveloppe le tarif dans un Optional
        when(tarifRepository.findByTypeJour("jour ordinaire"))
                .thenReturn(Optional.of(weekdayTarif)); // Enveloppe le tarif dans un Optional

        // Appeler la méthode à tester
        // *** CORRECTION: Utilise employeId (Long) ***
        double totalOvertimePay = employeService.calculateOvertime(employeId, startDate, endDate);

        // Vérifier le résultat
        // Record 1 (weekend): 2.0 heures * 20.0 = 40.0
        // Record 2 (jour ordinaire): 3.0 heures * 15.0 = 45.0
        // Total = 40.0 + 45.0 = 85.0
        assertEquals(85.0, totalOvertimePay, 0.001); // 0.001 est la tolérance pour les doubles/floats
    }

    // *** Nouveau Test : Vérifier le cas où un tarif est manquant ***
    @Test
    void testCalculateOvertime_TarifNotFound_ThrowsException() {
        // Simuler les données
        Long employeId = 2L; // *** Utilise Long ***
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 28);

        // Un seul enregistrement pour simplifier
        HeuresSup record = new HeuresSup(103L, null, LocalDate.of(2025, 2, 10), 1.0f); // Lundi -> Jour ordinaire

        List<HeuresSup> overtimeRecords = List.of(record); // Java 9+ pour List.of

        // Configurer les mocks
        when(heuresSupRepository.findByEmployeIdAndDateBetween(employeId, startDate, endDate))
                .thenReturn(overtimeRecords);

        // *** CORRECTION: Simuler le cas où le tarif "jour ordinaire" n'est PAS trouvé ***
        when(tarifRepository.findByTypeJour("jour ordinaire"))
                .thenReturn(Optional.empty()); // Retourne un Optional vide

        // Appeler la méthode et vérifier qu'elle lance une exception
        // (car on utilise orElseThrow dans le service)
        assertThrows(RuntimeException.class, () -> {
            employeService.calculateOvertime(employeId, startDate, endDate);
        }, "Une exception RuntimeException devrait être lancée si le tarif est introuvable");

        // Optionnel : Vérifier le message de l'exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeService.calculateOvertime(employeId, startDate, endDate);
        });
        assertTrue(exception.getMessage().contains("Tarif non trouvé pour le type : jour ordinaire"));
    }
}