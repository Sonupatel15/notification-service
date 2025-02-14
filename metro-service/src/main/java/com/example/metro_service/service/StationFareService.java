package com.example.metro_service.service; // Correct package

import com.example.metro_service.dto.StationFareDTO;
import com.example.metro_service.exception.ResourceNotFoundException;
import com.example.metro_service.model.Station;
import com.example.metro_service.model.StationFare;
import com.example.metro_service.repository.StationFareRepository;
import com.example.metro_service.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StationFareService {

    private final StationFareRepository stationFareRepository;
    private final StationRepository stationRepository;
    private final RedisCacheService redisCacheService; // Inject RedisCacheService

    public StationFareService(StationFareRepository stationFareRepository, StationRepository stationRepository, RedisCacheService redisCacheService) {
        this.stationFareRepository = stationFareRepository;
        this.stationRepository = stationRepository;
        this.redisCacheService = redisCacheService;
    }

    @Transactional
    public StationFare addFare(StationFareDTO fareDTO) {
        Station sourceStation = stationRepository.findById(fareDTO.getSourceStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found!"));

        Station destinationStation = stationRepository.findById(fareDTO.getDestinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found!"));

        StationFare fare = new StationFare(sourceStation, destinationStation, fareDTO.getFare());
        fare = stationFareRepository.save(fare);

        redisCacheService.cacheStationFare(fare); // Cache the fare

        return fare;
    }

    public StationFare getFare(Long sourceStationId, Long destinationStationId) {
        // Try to get from cache first
        StationFare cachedFare = redisCacheService.getStationFareFromCache(sourceStationId, destinationStationId);
        if (cachedFare != null) {
            return cachedFare;
        }

        // If not in cache, fetch from database
        StationFare fare = stationFareRepository.findBySourceStationIdAndDestinationStationId(sourceStationId, destinationStationId);
        if (fare != null) {
            redisCacheService.cacheStationFare(fare); // Cache it for future requests
            return fare;
        } else {
            throw new ResourceNotFoundException("Fare not found for given stations.");
        }
    }
}
