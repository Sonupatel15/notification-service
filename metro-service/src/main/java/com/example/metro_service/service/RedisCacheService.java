package com.example.metro_service.service; // Correct package

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void cacheStation(Station station) {
        String key = "station:" + station.getId();
        redisTemplate.opsForValue().set(key, station);
    }

    public Station getStationFromCache(Long stationId) {
        String key = "station:" + stationId;
        return (Station) redisTemplate.opsForValue().get(key);
    }

    public void cacheStationFare(StationFare stationFare) {
        String key = "fare:" + stationFare.getSourceStation().getId() + ":" + stationFare.getDestinationStation().getId();
        redisTemplate.opsForValue().set(key, stationFare);
    }

    public StationFare getStationFareFromCache(Long sourceStationId, Long destinationStationId) {
        String key = "fare:" + sourceStationId + ":" + destinationStationId;
        return (StationFare) redisTemplate.opsForValue().get(key);
    }

    // ... other caching methods as needed
}
