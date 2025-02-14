package com.example.user_service.controller;

import com.example.user_service.model.Trip;
import com.example.user_service.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // Consider using DTOs for request/response bodies instead of directly using entities
    @PostMapping("/start")
    public ResponseEntity<Trip> startTrip(@RequestParam Long userId, @RequestParam Long metroCardId) {
        return ResponseEntity.ok(tripService.startTrip(userId, metroCardId));
    }

    @PostMapping("/end/{tripId}")
    public ResponseEntity<Trip> endTrip(@PathVariable Long tripId, @RequestParam double fare) {
        return ResponseEntity.ok(tripService.endTrip(tripId, fare));
    }
}