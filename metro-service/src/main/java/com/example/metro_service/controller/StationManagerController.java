package com.example.metro_service.controller; // Correct package

import com.example.metro_service.model.StationManager;
import com.example.metro_service.service.StationManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/managers")
public class StationManagerController {

    private final StationManagerService managerService; // Use final and constructor injection

    public StationManagerController(StationManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping
    public ResponseEntity<List<StationManager>> getAllManagers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    @PostMapping
    public ResponseEntity<StationManager> addManager(@RequestBody StationManager manager) {
        return ResponseEntity.ok(managerService.addManager(manager));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) { // Return 204 No Content
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }
}
