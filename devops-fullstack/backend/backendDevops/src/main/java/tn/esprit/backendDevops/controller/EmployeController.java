package tn.esprit.backendDevops.controller;


import tn.esprit.backendDevops.dto.EmployeDTO;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.mappers.EmployeMapper;
import tn.esprit.backendDevops.services.EmployeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    private final EmployeService employeService;
    private final EmployeMapper employeMapper;

    public EmployeController(EmployeService employeService, EmployeMapper employeMapper) {
        this.employeService = employeService;
        this.employeMapper = employeMapper;
    }

    @GetMapping
    public List<EmployeDTO> getAllEmployes() {
        List<Employe> employes = employeService.getAllEmployes();
        return employes.stream()
                .map(employeMapper::employeToEmployeDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmployeDTO getEmployeById(@PathVariable Long id) {
        Employe employe = employeService.getEmployeById(id);
        return employeMapper.employeToEmployeDTO(employe);
    }

    @PostMapping
    public EmployeDTO createEmploye(@RequestBody EmployeDTO employeDTO) {
        Employe employe = employeMapper.employeDTOtoEmploye(employeDTO);
        Employe createdEmploye = employeService.createEmploye(employe);
        return employeMapper.employeToEmployeDTO(createdEmploye);
    }

    // Add PUT and DELETE methods as needed
}
