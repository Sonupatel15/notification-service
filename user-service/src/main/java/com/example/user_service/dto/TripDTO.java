package com.example.user_service.dto;

import com.example.user_service.model.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double fare;
    private TripStatus status;
    private Long metroCardId;
}