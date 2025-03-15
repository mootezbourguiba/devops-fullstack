package tn.esprit.backendDevops.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.repository.EmployeRepository;
import tn.esprit.backendDevops.services.EmployeService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class EmployeControllerIntegrationTest {

    @Autowired
    private EmployeRepository employeRepo;
    @Autowired
    private EmployeService employeService;
    @Test
    void testCreateEmploye() throws Exception {

        Employe employe = new Employe();
        employe.setNom("TestNom");
        employe= employeService.createEmploye(employe);

        org.assertj.core.api.Assertions.assertThat(employe.getNom()).isEqualTo("TestNom");

    }
}