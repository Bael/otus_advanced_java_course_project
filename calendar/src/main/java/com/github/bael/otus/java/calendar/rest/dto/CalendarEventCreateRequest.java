package com.github.bael.otus.java.calendar.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class CalendarEventCreateRequest {

    @NotNull(message = "Calendar ID is mandatory")
    private UUID calendarId;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotNull(message = "Start time is mandatory")
    private ZonedDateTime startTime;

    @NotNull(message = "End time is mandatory")
    private ZonedDateTime endTime;

    private Boolean allDay = false;

    private String eventType;

    private String location;

    private String scheduleCronExpression;

    private String status;
}