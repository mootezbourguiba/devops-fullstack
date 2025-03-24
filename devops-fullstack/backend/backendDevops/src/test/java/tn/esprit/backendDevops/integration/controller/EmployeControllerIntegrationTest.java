package tn.esprit.backendDevops.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.repository.EmployeRepository;
import tn.esprit.backendDevops.services.EmployeService;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(EmployeService.class) // Import EmployeService
class EmployeControllerIntegrationTest {

    @Autowired
    private EmployeRepository employeRepo;

    @Autowired
    private EmployeService employeService;

    @Test
    void testCreateEmploye() throws Exception {

        Employe employe = new Employe();
        employe.setNom("TestNom");
        employe.setPrenom("TestPrenom");
        Employe createdEmploye = null;
        try{
            createdEmploye= employeService.createEmploye(employe);
            assertNotNull(createdEmploye.getId());
        } catch (DataIntegrityViolationException e) {
            fail("Data integrity exception, maybe constraints not met");
        }


        assertThat(employe.getNom()).isEqualTo("TestNom");

    }

}