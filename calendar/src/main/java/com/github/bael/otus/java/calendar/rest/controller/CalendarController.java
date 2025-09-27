package com.github.bael.otus.java.calendar.rest.controller;

import com.github.bael.otus.java.calendar.domain.CalendarService;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.rest.dto.CalendarCreateRequest;
import com.github.bael.otus.java.calendar.rest.dto.CalendarResponse;
import com.github.bael.otus.java.calendar.rest.mapper.CalendarMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendar Management", description = "APIs for managing calendars")
public class CalendarController {

    private final CalendarService calendarService;
    private final CalendarMapper calendarMapper;

    @Operation(summary = "Get user calendars")
    @GetMapping("/user/{ownerId}")
    public List<CalendarResponse> getUserCalendars(@PathVariable UUID ownerId) {
        return calendarService.getUserCalendars(ownerId).stream()
                .map(calendarMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get calendar by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CalendarResponse> getCalendarById(@PathVariable UUID id) {
        return calendarService.getCalendarById(id)
                .map(calendarMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new calendar")
    @PostMapping
    public ResponseEntity<CalendarResponse> createCalendar(@Valid @RequestBody CalendarCreateRequest request) {
        Calendar calendar = calendarMapper.toEntity(request);
        Calendar savedCalendar = calendarService.createCalendar(calendar);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarMapper.toResponse(savedCalendar));
    }

    @Operation(summary = "Delete calendar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable UUID id) {
        if (calendarService.calendarExists(id)) {
            calendarService.deleteCalendar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}