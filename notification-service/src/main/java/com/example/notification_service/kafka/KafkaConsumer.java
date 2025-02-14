package com.example.notification_service.kafka;
import com.example.payment_service.model.Payment;


 // Import Payment class
import com.example.notification_service.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "payment_events", groupId = "notification-group")
    public void consumePaymentSuccess(ConsumerRecord<String, Object> record) { // Change value type to Object
        log.info("Received Kafka message: {}", record.value());

        Payment payment = (Payment) record.value(); // Cast to Payment object

        // Extract user email and message (you'll need to fetch the email based on userId)
        Long userId = payment.getUserId();
        String message = "Payment of " + payment.getAmount() + " successful!"; // Customize message

        // Assuming you have a way to get the user's email by userId
        String userEmail = getUserEmail(userId); // Implement getUserEmail()

        if (userEmail != null) {
            emailService.sendEmail(userEmail, "Payment Successful", message);
            log.info("Payment success email sent to {}", userEmail);
        } else {
            log.warn("User email not found for userId: {}", userId);
        }
    }

    // ... (SOS and penalty charge listeners - no changes needed)

    private String getUserEmail(Long userId) {
        // Replace this with your actual logic to fetch the user's email.
        // This might involve a call to your user service or database.
        // For example, using RestTemplate:
        // try {
        //     User user = restTemplate.getForObject("http://user-service/users/" + userId, User.class);
        //     if (user != null) {
        //         return user.getEmail();
        //     }
        // } catch (Exception e) {
        //     log.error("Error fetching user email: {}", e.getMessage());
        // }
        return null; // Return null if email not found
    }
}
