package com.example.user_service.repository;

import com.example.user_service.model.MetroCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetroCardRepository extends JpaRepository<MetroCard, Long> {
    Optional<MetroCard> findByUser_Id(Long userId); // Find by user ID
}