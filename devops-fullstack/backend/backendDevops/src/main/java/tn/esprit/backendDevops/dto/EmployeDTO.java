package tn.esprit.backendDevops.dto;

import lombok.Data;

@Data
public class EmployeDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String poste;
}