package com.github.bael.otus.java.calendar.rest.dto;

import com.github.bael.otus.java.calendar.interop.user.UserInfo;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CalendarEventResponse {
    private UUID id;
    private UUID calendarId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allDay;
    private String eventType;
    private String location;
    private String scheduleCronExpression;
    private UUID parentEventId;
    private String status;
    private Instant createdAt;
    private List<UserInfo> attendees;


}