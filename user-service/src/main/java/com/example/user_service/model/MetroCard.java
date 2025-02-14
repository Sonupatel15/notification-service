package com.example.user_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metro_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetroCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardNo;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
