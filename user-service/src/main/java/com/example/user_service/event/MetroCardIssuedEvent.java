package com.example.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetroCardIssuedEvent {
    private Long cardId;   // Change from String to Long
    private Long userId;
    private double balance;
}
