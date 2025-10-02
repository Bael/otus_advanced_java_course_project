package com.github.bael.otus.java.calendar.rest.controller;

// com.github.bael.otus.java.calendar.controller.WeekdayScheduleController


import com.github.bael.otus.java.calendar.domain.CalendarWeekdayPeriodService;


import com.github.bael.otus.java.calendar.rest.dto.WeekdayPeriodRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calendar/")
@RequiredArgsConstructor
@Tag(name = "Weekly Schedule", description = "Manage user's weekly availability")
public class WeekdayScheduleController {

    private final CalendarWeekdayPeriodService scheduleService;

    @Operation(
            summary = "Set user's weekly availability schedule",
            description = "Replaces all existing weekly periods for the user with the provided list."
    )
    @ApiResponse(responseCode = "204", description = "Schedule updated successfully")
    @PutMapping("/{calendarId}/{userId}")
    public ResponseEntity<Void> setWeeklySchedule(
            @PathVariable UUID calendarId,
            @PathVariable UUID userId,
            @RequestBody WeekdayPeriodRequest request
    ) {
        scheduleService.setWeeklySchedule(calendarId, userId, request);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get user's weekly availability schedule",
            description = "Get all existing weekly periods by the user."
    )
    @ApiResponse(responseCode = "200", description = "Got successfully")
    @GetMapping("/{calendarId}/{userId}")
    public List<WeekdayPeriodRequest.Period> getWeeklySchedule(
            @PathVariable UUID calendarId,
            @PathVariable UUID userId) {
        return scheduleService.getWeeklySchedules(calendarId, userId).stream()
                .map(period ->
                    new WeekdayPeriodRequest.Period(period.getWeekDay(),
                            period.getStart(), period.getFinish())).toList();
    }
}
