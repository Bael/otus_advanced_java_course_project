package com.github.bael.otus.java.calendar.rest.dto;

import lombok.Data;

import java.time.Instant;

import java.util.UUID;

@Data
public class CalendarResponse {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String color;
    private Boolean isPublic;
    private Instant createdAt;
}