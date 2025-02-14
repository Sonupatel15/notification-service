package com.example.metro_service.controller; // Correct package

import com.example.metro_service.exception.UserNotFoundException;
import com.example.metro_service.model.TravelHistory;
import com.example.metro_service.model.User;
import com.example.metro_service.repository.TravelHistoryRepository;
import com.example.metro_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/metro")
@Slf4j
public class CheckInController {

    @Autowired
    private TravelHistoryRepository travelHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestParam Long userId, @RequestParam Long stationId) {
        log.info("Check-in request received for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getMetroCard() == null || !user.getMetroCard().isActive()) { // Correct check for valid card
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid metro card.");
        }

        // Check if the user is already checked in (using repository method)
        if (travelHistoryRepository.existsByUserIdAndTravelStatus(userId, 0)) { // Assuming 0 is the "checked in" status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already checked in.");
        }

        // Ensure user has enough balance (Assume minimum fare is needed)
        if (user.getMetroCard().getBalance() < 10.0) { // Correctly access balance
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance.");
        }

        TravelHistory travel = new TravelHistory();
        travel.setUser(user); // Set the User object, not just ID
        travel.setCurrentStationId(stationId); // Store station ID
        travel.setInTime(LocalDateTime.now());
        travel.setTravelStatus(0);

        travelHistoryRepository.save(travel);
        log.info("Check-in successful for user: {}", userId);

        return ResponseEntity.ok("Check-in successful.");
    }
}
