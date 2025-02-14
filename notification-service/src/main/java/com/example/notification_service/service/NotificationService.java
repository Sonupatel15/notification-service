package com.example.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void listenPaymentEvent(String message) {
        System.out.println("Received Notification: " + message);
        // TODO: Integrate Email/SMS Notification here
    }
    @KafkaListener(topics = "sos_alerts", groupId = "notification-group")
    public void consumeSOSAlerts(ConsumerRecord<String, String> record) {
        String managerEmail = record.key();
        String message = record.value();
        emailService.sendEmail(managerEmail, "SOS Alert", message);
    }

    @KafkaListener(topics = "penalty_charged", groupId = "notification-group")
    public void consumePenaltyEvents(ConsumerRecord<String, String> record) {
        String userEmail = record.key();
        String message = record.value();
        emailService.sendEmail(userEmail, "Travel Penalty Charged", message);
    }
}
