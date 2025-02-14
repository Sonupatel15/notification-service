package com.example.user_service.service;

import com.example.user_service.client.PaymentClient; // Inject PaymentClient
import com.example.user_service.constant.Constants; // Import constants
import com.example.user_service.dto.PaymentRequest;
import com.example.user_service.exception.BusinessException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.*;
import com.example.user_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private static final Logger logger = LoggerFactory.getLogger(TripService.class);

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final MetroCardRepository metroCardRepository;
    private final PaymentClient paymentClient; // Inject PaymentClient
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Trip startTrip(Long userId, Long metroCardId) {
        logger.info("Starting trip for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        MetroCard metroCard = metroCardRepository.findById(metroCardId)
                .orElseThrow(() -> new ResourceNotFoundException("MetroCard not found"));

        Trip trip = Trip.builder()
                .user(user)
                .metroCard(metroCard)
                .startTime(LocalDateTime.now())
                .status(TripStatus.STARTED)
                .fare(0.0) // Initialize fare
                .build();

        trip = tripRepository.save(trip);

        // Publish trip started event
        kafkaTemplate.send(Constants.TOPIC_TRIP_STARTED, trip); // Or a specific DTO

        return trip;
    }

    @Transactional
    public Trip endTrip(Long tripId) { // Removed fare parameter, calculate here
        logger.info("Ending trip: {}", tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        trip.setEndTime(LocalDateTime.now());

        // Calculate fare (replace with your actual fare calculation logic)
        double fare = calculateFare(trip);
        trip.setFare(fare);

        // Process payment
        try {
            PaymentRequest paymentRequest = new PaymentRequest(trip.getUser().getId(), BigDecimal.valueOf(fare));
            String paymentResponse = paymentClient.processPayment(paymentRequest);

            if (!"SUCCESS".equals(paymentResponse)) { // Replace with actual payment status check
                throw new BusinessException("Payment failed");
            }

            trip.setStatus(TripStatus.COMPLETED); // Set status after successful payment
            tripRepository.save(trip);

            // Publish trip completed event
            kafkaTemplate.send(Constants.TOPIC_TRIP_COMPLETED, trip); // Or a DTO

        } catch (Exception e) {
            logger.error("Payment or trip completion failed: {}", e.getMessage());
            trip.setStatus(TripStatus.CANCELLED); // Or handle differently
            tripRepository.save(trip);
            throw new BusinessException("Trip completion failed: " + e.getMessage());
        }

        return trip;
    }

    private double calculateFare(Trip trip) {
        // Implement your fare calculation logic here.
        // This is a placeholder. You'll need to fetch route details,
        // apply peak hour pricing, discounts, etc.

        // Example: Flat fare for now
        return 20.0; // Replace with your actual fare calculation
    }

    public List<Trip> getTravelHistory(Long userId) {
        return tripRepository.findByUserIdOrderByStartTimeDesc(userId);
    }
}