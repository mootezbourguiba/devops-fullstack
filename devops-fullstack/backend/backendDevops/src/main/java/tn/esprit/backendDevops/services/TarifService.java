package tn.esprit.backendDevops.services;

import tn.esprit.backendDevops.entities.Tarif;
import tn.esprit.backendDevops.exceptions.ResourceNotFoundException;
import tn.esprit.backendDevops.repository.TarifRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarifService {

    private final TarifRepository tarifRepository;

    public TarifService(TarifRepository tarifRepository) {
        this.tarifRepository = tarifRepository;
    }

    public List<Tarif> getAllTarifs() {
        return tarifRepository.findAll();
    }

    public Tarif getTarifById(Long id) {
        return tarifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));
    }

    public Tarif createTarif(Tarif tarif) {
        return tarifRepository.save(tarif);
    }

    public Tarif updateTarif(Long id, Tarif tarifDetails) {
        Tarif tarif = tarifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));

        tarif.setTypeJour(tarifDetails.getTypeJour());
        tarif.setTarif(tarifDetails.getTarif());

        return tarifRepository.save(tarif);
    }

    public void deleteTarif(Long id) {
        Tarif tarif = tarifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));

        tarifRepository.delete(tarif);
    }
}