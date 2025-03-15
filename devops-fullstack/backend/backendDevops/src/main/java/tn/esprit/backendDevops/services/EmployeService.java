package tn.esprit.backendDevops.services;

import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.exceptions.ResourceNotFoundException; // Import exception
import tn.esprit.backendDevops.repository.EmployeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeService {

    private final EmployeRepository employeRepository;

    public EmployeService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public List<Employe> getAllEmployes() {
        return employeRepository.findAll();
    }

    public Employe getEmployeById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe not found with id: " + id));
    }

    public Employe createEmploye(Employe employe) {
        return employeRepository.save(employe);
    }

    public Employe updateEmploye(Long id, Employe employeDetails) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe not found with id: " + id));

        employe.setNom(employeDetails.getNom());
        employe.setPrenom(employeDetails.getPrenom());
        employe.setPoste(employeDetails.getPoste());

        return employeRepository.save(employe);
    }

    public void deleteEmploye(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe not found with id: " + id));

        employeRepository.delete(employe);
    }
}