package com.example.user_service.repository;

import com.example.user_service.model.Trip;
import com.example.user_service.model.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByUserIdAndStatus(Long userId, TripStatus status);
    List<Trip> findByUserIdOrderByStartTimeDesc(Long userId); // For travel history
}