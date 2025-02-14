package com.example.metro_service.controller; // Correct package

import com.example.metro_service.dto.StationFareDTO;
import com.example.metro_service.model.StationFare;
import com.example.metro_service.service.StationFareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/station-fare")
public class StationFareController {

    private final StationFareService stationFareService; // Use final and constructor injection

    public StationFareController(StationFareService stationFareService) {
        this.stationFareService = stationFareService;
    }

    @PostMapping
    public ResponseEntity<StationFare> addFare(@RequestBody StationFareDTO fareDTO) {
        return ResponseEntity.ok(stationFareService.addFare(fareDTO));
    }

    @GetMapping
    public ResponseEntity<StationFare> getFare(
            @RequestParam Long sourceStationId,
            @RequestParam Long destinationStationId) {
        return ResponseEntity.ok(stationFareService.getFare(sourceStationId, destinationStationId));
    }
}
