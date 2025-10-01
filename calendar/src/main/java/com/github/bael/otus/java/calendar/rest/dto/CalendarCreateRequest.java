package com.github.bael.otus.java.calendar.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class CalendarCreateRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private String color;

    private UUID ownerId;
}