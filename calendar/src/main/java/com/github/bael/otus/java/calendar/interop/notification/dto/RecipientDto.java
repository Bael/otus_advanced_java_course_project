package com.github.bael.otus.java.calendar.interop.notification.dto;

import java.util.UUID;

public record RecipientDto(
        UUID userId,
        String email,
        String fullName
) {}