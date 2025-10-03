package com.github.bael.otus.java.rest.dto;


import java.time.Instant;

public record EventNotificationRequest(
        RecipientDto recipient,
        String eventType,
        Instant eventTime,
        String message
) {}