package com.github.bael.otus.java.calendar.interop.notification.dto;


import java.time.Instant;

public record EventNotificationRequest(
        RecipientDto recipient,
        String eventType,
        Instant eventTime,
        String message
) {}