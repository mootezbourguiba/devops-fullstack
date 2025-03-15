package tn.esprit.backendDevops.services;


import tn.esprit.backendDevops.entities.HeuresSup;
import tn.esprit.backendDevops.exceptions.ResourceNotFoundException;
import tn.esprit.backendDevops.repository.HeuresSupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeuresSupService {

    private final HeuresSupRepository heuresSupRepository;

    public HeuresSupService(HeuresSupRepository heuresSupRepository) {
        this.heuresSupRepository = heuresSupRepository;
    }

    public List<HeuresSup> getAllHeuresSup() {
        return heuresSupRepository.findAll();
    }

    public HeuresSup getHeuresSupById(Long id) {
        return heuresSupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HeuresSup not found with id: " + id));
    }

    public HeuresSup createHeuresSup(HeuresSup heuresSup) {
        return heuresSupRepository.save(heuresSup);
    }

    // Add update and delete methods as needed
}
