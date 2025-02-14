package com.example.user_service.service;

import com.example.user_service.client.PaymentClient;
import com.example.user_service.constant.Constants;
import com.example.user_service.dto.PaymentRequest;
import com.example.user_service.event.MetroCardIssuedEvent;
import com.example.user_service.exception.BusinessException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.MetroCard;
import com.example.user_service.model.User;
import com.example.user_service.repository.MetroCardRepository;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetroCardService {
    private static final Logger logger = LoggerFactory.getLogger(MetroCardService.class);

    private final MetroCardRepository metroCardRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public MetroCard buyMetroCard(Long userId, double balance) {
        logger.info("Processing metro card purchase for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Optional<MetroCard> existingCard = metroCardRepository.findByUser_Id(userId);

        if (existingCard.isPresent() && existingCard.get().isActive()) {
            throw new BusinessException("User already has an active metro card");
        }

        try {
            // Process payment (Simplified for demo)
            PaymentRequest paymentRequest = new PaymentRequest(userId, BigDecimal.valueOf(balance));
            String paymentResponse = paymentClient.processPayment(paymentRequest); // Replace with actual payment logic
            if (!"SUCCESS".equals(paymentResponse)) {  // Replace with actual payment status check
                throw new BusinessException("Payment failed for metro card purchase");
            }

            MetroCard metroCard;
            if (existingCard.isPresent()) {
                metroCard = reactivateCard(existingCard.get(), balance);
            } else {
                metroCard = createNewCard(user, balance);
            }

            // Publish event (after successful payment and card creation/reactivation)
            kafkaTemplate.send(Constants.TOPIC_METRO_CARD_ISSUED,
                    new MetroCardIssuedEvent(metroCard.getCardNo(), userId, balance));

            logger.info("Successfully issued/recharged metro card for user: {}", userId);
            return metroCard;

        } catch (Exception e) {
            logger.error("Error while processing metro card purchase for user: {}", userId, e);
            throw new BusinessException("Failed to process metro card purchase: " + e.getMessage());
        }
    }

    private MetroCard createNewCard(User user, double balance) {
        MetroCard metroCard = new MetroCard();
        metroCard.setBalance(balance);
        metroCard.setUser(user);
        metroCard.setActive(true);
        return metroCardRepository.save(metroCard);
    }

    private MetroCard reactivateCard(MetroCard card, double balance) {
        card.setActive(true);
        card.setBalance(balance);
        return metroCardRepository.save(card);
    }
}