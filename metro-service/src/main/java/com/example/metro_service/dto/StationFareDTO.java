package com.example.metro_service.dto; // Correct package

import lombok.Data;

@Data
public class StationFareDTO {
    private Long sourceStationId;
    private Long destinationStationId;
    private Double fare;
}