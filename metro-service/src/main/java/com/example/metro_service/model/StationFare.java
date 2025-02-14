package com.example.metro_service.model; // Correct package

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "station_fare")
@Getter
@Setter
@NoArgsConstructor
public class StationFare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;

    @ManyToOne
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Column(nullable = false)
    private Double fare;

    public StationFare(Station sourceStation, Station destinationStation, Double fare) {
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.fare = fare;
    }
}
