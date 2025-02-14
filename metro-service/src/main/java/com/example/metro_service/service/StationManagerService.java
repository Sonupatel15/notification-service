package com.example.metro_service.service; // Correct package

import com.example.metro_service.model.StationManager;
import com.example.metro_service.repository.StationManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationManagerService {

    private final StationManagerRepository managerRepository;

    public StationManagerService(StationManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public List<StationManager> getAllManagers() {
        return managerRepository.findAll();
    }

    public StationManager addManager(StationManager manager) {
        return managerRepository.save(manager);
    }

    public void deleteManager(Long id) {
        managerRepository.deleteById(id);
    }
}
