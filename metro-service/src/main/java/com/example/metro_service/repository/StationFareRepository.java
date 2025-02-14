package com.example.metro_service.repository; // Correct package

import com.example.metro_service.model.StationFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationFareRepository extends JpaRepository<StationFare, Long> {
    StationFare findBySourceStationIdAndDestinationStationId(Long sourceStationId, Long destinationStationId);
}