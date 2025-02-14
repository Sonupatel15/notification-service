package com.example.user_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "metro_card_id", nullable = false)
    private MetroCard metroCard;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false) // Should not be nullable
    private double fare;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @Column(name = "sos_requested") // Add SOS column
    private boolean sosRequested;
}