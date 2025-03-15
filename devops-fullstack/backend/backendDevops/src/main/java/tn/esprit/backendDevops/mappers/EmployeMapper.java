package tn.esprit.backendDevops.mappers;

import tn.esprit.backendDevops.dto.EmployeDTO;
import tn.esprit.backendDevops.entities.Employe;

public interface EmployeMapper {
    EmployeDTO employeToEmployeDTO(Employe employe);
    Employe employeDTOtoEmploye(EmployeDTO employeDTO);
}