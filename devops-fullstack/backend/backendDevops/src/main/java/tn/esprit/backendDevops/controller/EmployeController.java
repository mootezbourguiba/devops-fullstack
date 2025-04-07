package tn.esprit.backendDevops.controller; // Garde ton nom de package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity; // Import pour ResponseEntity
import org.springframework.web.bind.annotation.*;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.services.EmployeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // Import pour Map.of

@RestController
@RequestMapping("/api/employees") // Nommage cohérent
@CrossOrigin(origins = "*") // Garde ta config CrossOrigin
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    // Endpoint pour récupérer tous les employés
    // Convention : @GetMapping sans argument pour mapper à /api/employees
    @GetMapping
    public List<Employe> getAllEmployes() {
        return employeService.getAllEmployes();
    }

    // Endpoint pour calculer les heures supplémentaires (version améliorée)
    @GetMapping("/{employeId}/overtime") // Nommage employeId et type Long
    public ResponseEntity<?> calculateOvertimePay(
            @PathVariable Long employeId, // Utilise Long ici
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            // Appelle la méthode du service
            double totalPay = employeService.calculateOvertime(employeId, startDate, endDate);
            // Retourne une réponse OK (200) avec un corps JSON standardisé
            return ResponseEntity.ok(Map.of("totalPay", totalPay));
        } catch (RuntimeException e) {
            // Gère les erreurs connues (ex: Tarif non trouvé levé par le service)
            // Retourne une réponse Bad Request (400) avec le message d'erreur
            // Tu pourrais affiner le code de statut (ex: 404 si l'employé n'existe pas)
            System.err.println("Erreur lors du calcul des heures sup pour l'employé " + employeId + ": " + e.getMessage()); // Log de l'erreur côté serveur
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Gère les autres erreurs inattendues
            System.err.println("Erreur interne lors du calcul des heures sup: " + e.getMessage()); // Log de l'erreur côté serveur
            return ResponseEntity.internalServerError().body(Map.of("error", "Une erreur interne est survenue lors du calcul."));
        }
    }

    // Optionnel: Endpoint POST pour créer un employé (si tu l'as ou veux l'ajouter)
    // @PostMapping
    // public Employe createEmploye(@RequestBody Employe employe) {
    //     return employeService.createEmploye(employe);
    // }
}