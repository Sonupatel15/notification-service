package com.example.metro_service.model; // Correct package

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "station_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StationManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
}
