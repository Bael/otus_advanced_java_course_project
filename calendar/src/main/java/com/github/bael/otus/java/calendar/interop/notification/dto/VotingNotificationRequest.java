package com.github.bael.otus.java.calendar.interop.notification.dto;

import java.util.List;

public record VotingNotificationRequest(
        RecipientDto recipient,
        String pollTitle,
        String pollDescription,
        List<VotingSlotDto> slots
) {}