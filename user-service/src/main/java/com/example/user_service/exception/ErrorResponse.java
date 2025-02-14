package com.example.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private String timestamp;
    private int status;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
        this.status = 500; // Default status code
    }
}