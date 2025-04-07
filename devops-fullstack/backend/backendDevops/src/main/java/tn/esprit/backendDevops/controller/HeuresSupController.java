package tn.esprit.backendDevops.controller; // Assure-toi que le package est correct

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.entities.HeuresSup;
// Pour l'instant, on injecte directement les repositories.
// Idéalement, on créerait des HeuresSupService/TarifService.
import tn.esprit.backendDevops.repository.EmployeRepository;
import tn.esprit.backendDevops.repository.HeuresSupRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/heures-sup") // Chemin de base pour ce contrôleur
@CrossOrigin(origins = "*") // Garde ta config CrossOrigin
public class HeuresSupController {

    @Autowired
    private HeuresSupRepository heuresSupRepository;

    @Autowired
    private EmployeRepository employeRepository; // Nécessaire pour vérifier et associer l'employé

    /**
     * Endpoint pour ajouter un enregistrement d'heures supplémentaires.
     * Le corps de la requête doit contenir un JSON représentant HeuresSup,
     * avec au moins la date, nbHeures et un objet 'employe' contenant juste l'ID.
     * Exemple JSON Body:
     * {
     *   "date": "2024-04-10",
     *   "nbHeures": 2.5,
     *   "employe": {
     *     "id": 1
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<?> addHeureSup(@RequestBody HeuresSup heureSupRequest) {
        // --- Validation ---
        if (heureSupRequest.getEmploye() == null || heureSupRequest.getEmploye().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "L'ID de l'employé est requis dans l'objet 'employe'."));
        }
        if (heureSupRequest.getDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "La date est requise."));
        }
        if (heureSupRequest.getNbHeures() <= 0) { // On suppose que les heures doivent être positives
            return ResponseEntity.badRequest().body(Map.of("error", "Le nombre d'heures doit être positif."));
        }

        // Vérifier si l'employé existe
        Optional<Employe> employeOpt = employeRepository.findById(heureSupRequest.getEmploye().getId());
        if (!employeOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 Not Found
                    .body(Map.of("error", "Employé non trouvé avec l'ID : " + heureSupRequest.getEmploye().getId()));
        }

        // --- Préparation et Sauvegarde ---
        // Créer une nouvelle instance pour être sûr de ne pas modifier des champs non désirés
        HeuresSup nouvelleHeureSup = new HeuresSup();
        nouvelleHeureSup.setEmploye(employeOpt.get()); // Associer l'employé complet trouvé en DB
        nouvelleHeureSup.setDate(heureSupRequest.getDate());
        nouvelleHeureSup.setNbHeures(heureSupRequest.getNbHeures());

        try {
            HeuresSup savedHeureSup = heuresSupRepository.save(nouvelleHeureSup);
            // Retourne une réponse CREATED (201) avec l'objet sauvegardé
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHeureSup);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde des heures sup: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur interne lors de la sauvegarde des heures supplémentaires."));
        }
    }

    // Optionnel: Endpoint pour récupérer toutes les heures sup (utile pour le debug)
    @GetMapping
    public List<HeuresSup> getAllHeuresSup() {
        // Attention: peut retourner beaucoup de données. Ajouter de la pagination serait mieux.
        return heuresSupRepository.findAll();
    }

    // Optionnel: Endpoint pour récupérer les heures sup d'un employé spécifique
    @GetMapping("/employee/{employeId}")
    public ResponseEntity<?> getHeuresSupByEmployee(@PathVariable Long employeId) {
        Optional<Employe> employeOpt = employeRepository.findById(employeId);
        if (!employeOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Employé non trouvé avec l'ID : " + employeId));
        }

        // Utilise la méthode du repository que nous avons définie plus tôt
        List<HeuresSup> heures = heuresSupRepository.findByEmployeIdAndDateBetween(employeId, LocalDate.MIN, LocalDate.MAX); // Ou une méthode findByEmployeId si tu la crées
        return ResponseEntity.ok(heures);
    }
}