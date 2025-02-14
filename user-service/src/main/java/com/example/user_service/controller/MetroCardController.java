package com.example.user_service.controller;

import com.example.user_service.model.MetroCard;
import com.example.user_service.service.MetroCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards") // More appropriate path
public class MetroCardController {

    private final MetroCardService metroCardService; // Use final and constructor injection

    public MetroCardController(MetroCardService metroCardService) {
        this.metroCardService = metroCardService;
    }


    @PostMapping
    public ResponseEntity<MetroCard> buyMetroCard(@RequestParam Long userId, @RequestParam double balance) {
        return ResponseEntity.ok(metroCardService.buyMetroCard(userId, balance));
    }
}
