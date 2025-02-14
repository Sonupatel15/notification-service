package com.example.metro_service.service; // Correct package

import com.example.metro_service.exception.ResourceNotFoundException;
import com.example.metro_service.model.Station;
import com.example.metro_service.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final RedisCacheService redisCacheService; // Inject RedisCacheService

    public StationService(StationRepository stationRepository, RedisCacheService redisCacheService) {
        this.stationRepository = stationRepository;
        this.redisCacheService = redisCacheService;
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Transactional
    public Station addStation(Station station) {
        station = stationRepository.save(station);
        redisCacheService.cacheStation(station); // Cache the station
        return station;
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
        redisCacheService.deleteStationFromCache(id); // Remove from cache
    }

    public Station getStationById(Long id) {
        Station cachedStation = redisCacheService.getStationFromCache(id);
        if (cachedStation != null) {
            return cachedStation;
        }
        Station station = stationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        redisCacheService.cacheStation(station);
        return station;
    }

    private void deleteStationFromCache(Long id) {
        String key = "station:" + id;
        redisCacheService.deleteFromCache(key);
    }

    private void deleteFromCache(String key) {
        redisCacheService.deleteFromCache(key);
    }
}
