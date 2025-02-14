package com.example.metro_service.controller; // Correct package

import com.example.metro_service.exception.ResourceNotFoundException;
import com.example.metro_service.model.StationManager;
import com.example.metro_service.model.TravelHistory;
import com.example.metro_service.repository.StationManagerRepository;
import com.example.metro_service.repository.TravelHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sos")
@Slf4j
public class SOSController {

    @Autowired
    private TravelHistoryRepository travelRepo;

    @Autowired
    private StationManagerRepository managerRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; // Correct KafkaTemplate type

    @PostMapping
    public ResponseEntity<?> triggerSOS(@RequestParam Long travelId) {
        TravelHistory travel = travelRepo.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found"));

        if (travel.getTravelStatus() != 0) {
            return ResponseEntity.badRequest().body("SOS only allowed during active travel");
        }

        StationManager manager = managerRepo.findByStationId(travel.getCurrentStationId()) // Use getCurrentStationId
                .orElseThrow(() -> new ResourceNotFoundException("Station manager not found"));

        String message = String.format("SOS Alert! Travel ID: %d, Station ID: %d",
                travelId, travel.getCurrentStationId());

        // Send a more structured message (e.g., a JSON object) for easier parsing by the Notification Service
        // Example:
        // SOSAlertEvent sosAlertEvent = new SOSAlertEvent(travelId, travel.getCurrentStationId(), manager.getEmail());
        // kafkaTemplate.send("sos_alerts", sosAlertEvent); // Send the event object

        kafkaTemplate.send("sos_alerts", manager.getEmail(), message); // Or send the simple message

        return ResponseEntity.ok("SOS alert sent to station manager");
    }
}
