package tn.esprit.backendDevops.unit.service;

import tn.esprit.backendDevops.entities.Employe;
import tn.esprit.backendDevops.repository.EmployeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.backendDevops.services.EmployeService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private EmployeService employeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployes() {
        Employe employe1 = new Employe();
        employe1.setId(1L);
        employe1.setNom("Doe");
        employe1.setPrenom("John");
        employe1.setPoste("Developer");

        Employe employe2 = new Employe();
        employe2.setId(2L);
        employe2.setNom("Smith");
        employe2.setPrenom("Jane");
        employe2.setPoste("Manager");

        when(employeRepository.findAll()).thenReturn(Arrays.asList(employe1, employe2));

        List<Employe> employes = employeService.getAllEmployes();

        assertEquals(2, employes.size());
        assertEquals("Doe", employes.get(0).getNom());
        assertEquals("Smith", employes.get(1).getNom());

        verify(employeRepository, times(1)).findAll();
    }

    @Test
    public void testGetEmployeById() {
        Employe employe = new Employe();
        employe.setId(1L);
        employe.setNom("Doe");
        employe.setPrenom("John");
        employe.setPoste("Developer");

        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));

        Employe retrievedEmploye = employeService.getEmployeById(1L);

        assertNotNull(retrievedEmploye);
        assertEquals("Doe", retrievedEmploye.getNom());
        verify(employeRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateEmploye() {
        Employe employe = new Employe();
        employe.setNom("Doe");
        employe.setPrenom("John");
        employe.setPoste("Developer");

        when(employeRepository.save(employe)).thenReturn(employe);

        Employe createdEmploye = employeService.createEmploye(employe);

        assertNotNull(createdEmploye);
        assertEquals("Doe", createdEmploye.getNom());
        verify(employeRepository, times(1)).save(employe);
    }

    // Add more test methods for updateEmploye, deleteEmploye, etc.
}