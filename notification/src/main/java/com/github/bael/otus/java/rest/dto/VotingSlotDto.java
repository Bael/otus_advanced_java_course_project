package com.github.bael.otus.java.rest.dto;

import java.time.Instant;

public record VotingSlotDto(
        Instant startTime,
        Instant endTime,
        String callbackUrl
) {}