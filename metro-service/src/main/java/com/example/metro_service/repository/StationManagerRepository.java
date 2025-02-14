package com.example.metro_service.repository; // Correct package

import com.example.metro_service.model.StationManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationManagerRepository extends JpaRepository<StationManager, Long> {
    Optional<StationManager> findByStationId(Long stationId);
}
