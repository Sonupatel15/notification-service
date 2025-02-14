package com.example.metro_service.controller; // Correct package

import com.example.metro_service.client.PaymentClient; // Import PaymentClient
import com.example.metro_service.dto.PaymentRequest;
import com.example.metro_service.exception.TravelNotFoundException;
import com.example.metro_service.model.TravelHistory;
import com.example.metro_service.repository.StationRepository; // Import StationRepository
import com.example.metro_service.repository.TravelHistoryRepository;
import com.example.metro_service.service.FareCalculationService; // Import FareCalculationService
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; // Import RestTemplate

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/metro")
@Slf4j
public class CheckoutController {

    @Autowired
    private TravelHistoryRepository travelHistoryRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private FareCalculationService fareCalculationService;

    @Autowired
    private PaymentClient paymentClient; // Inject PaymentClient

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@RequestParam Long travelId, @RequestParam Long stationId) {
        log.info("Check-out request received for travel ID: {}", travelId);


        TravelHistory travel = travelHistoryRepository.findById(travelId)
                .orElseThrow(() -> new TravelNotFoundException("Travel record not found"));

        // Prevent double check-out
        if (travel.getTravelStatus() == 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Travel already checked out.");
        }

        travel.setOutTime(LocalDateTime.now());
        long duration = Duration.between(travel.getInTime(), travel.getOutTime()).toMinutes();

        // Ensure station exists
        if (!stationRepository.existsById(stationId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid station.");
        }

        double fare = fareCalculationService.calculateFare(travel.getCurrentStationId(), stationId); // Use IDs
        double extraCharge = (duration > 90) ? fare * 0.2 : 0;
        double finalFare = fare + extraCharge;

        // Call Payment Service (Handle failure cases)
        PaymentRequest payment = new PaymentRequest(travel.getUser().getId(), finalFare); // Use user ID from TravelHistory
        try {
            String paymentResponse = paymentClient.processPayment(payment); // Use PaymentClient

            if (!"SUCCESS".equals(paymentResponse)) { // Replace with actual payment status check
                throw new Exception("Payment failed"); // Or a custom exception
            }

        } catch (Exception e) {
            log.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed.");
        }

        travel.setTotalTime(duration);
        travel.setTravelStatus(1);
        travelHistoryRepository.save(travel);

        log.info("Check-out successful. Total Fare: {}", finalFare);
        return ResponseEntity.ok("Check-out successful. Fare: " + finalFare);
    }
}