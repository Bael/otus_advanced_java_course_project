package com.github.bael.otus.java.calendar.interop.notification.dto;

import java.time.Instant;

public record VotingSlotDto(
        Instant startTime,
        Instant endTime,
        String callbackUrl
) {}