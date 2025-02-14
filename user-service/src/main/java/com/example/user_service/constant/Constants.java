package com.example.user_service.constant;

public class Constants {
    public static final String REDIS_USER_KEY_PREFIX = "user:";
    public static final String REDIS_METRO_CARD_KEY_PREFIX = "metro_card:";
    public static final String REDIS_TRIP_KEY_PREFIX = "trip:";
    public static final long CACHE_DURATION_HOURS = 24;

    // Kafka Topics
    public static final String TOPIC_USER_CREATED = "user.created";
    public static final String TOPIC_METRO_CARD_ISSUED = "metro.card.issued";
    public static final String TOPIC_TRIP_STARTED = "trip.started";
    public static final String TOPIC_TRIP_COMPLETED = "trip.completed";
}
