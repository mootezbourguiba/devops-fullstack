package tn.esprit.backendDevops.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.services.EmployeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees") //Utilisez employees au lieu de employes. Pour homogénéiser le code
@CrossOrigin(origins = "*")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    @GetMapping("/") // Route pour récupérer tous les employés (GET /api/employees)
    public List<Employe> getAllEmployes() {
        return employeService.getAllEmployes(); //Supprimer tout le reste, c'est ça le problème du back!
    }

    @GetMapping("/{id}/overtime")
    public double getOvertime(
            @PathVariable int id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return employeService.calculateOvertime(id, startDate, endDate);
    }
}