package com.github.bael.otus.java.calendar.rest.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ScheduleMeetingRequest {
    private String title;
    private int hours;
    private UUID organizerId;
    private List<UUID> attendees;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

}

