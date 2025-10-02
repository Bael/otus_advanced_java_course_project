package com.github.bael.otus.java.calendar.rest.dto;


import com.github.bael.otus.java.calendar.interop.user.UserInfo;

import java.time.Instant;
import java.util.List;


public record CalendarEventDto(
        String id,
        String calendarId,
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        String eventType,
        String location,
        String status,
        String ownerName, // обогащённое поле
        String ownerTimezone,
        List<UserInfo> attendees
) {}