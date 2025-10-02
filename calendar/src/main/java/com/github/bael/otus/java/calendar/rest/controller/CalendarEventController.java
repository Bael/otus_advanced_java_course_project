package com.github.bael.otus.java.calendar.rest.controller;

// com.github.bael.otus.java.calendar.controller.CalendarEventController

import com.github.bael.otus.java.calendar.data.CalendarEventAttendeeRepository;
import com.github.bael.otus.java.calendar.domain.AvailabilityService;
import com.github.bael.otus.java.calendar.domain.CalendarEventService;
import com.github.bael.otus.java.calendar.domain.CalendarService;
import com.github.bael.otus.java.calendar.domain.MeetingService;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarEventAttendee;

import com.github.bael.otus.java.calendar.interop.user.UserServiceClient;
import com.github.bael.otus.java.calendar.rest.dto.*;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;


import com.github.bael.otus.java.calendar.rest.mapper.CalendarEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/calendar/events")
@RequiredArgsConstructor
public class CalendarEventController {

    private final CalendarEventService calendarEventService;
    private final CalendarService calendarService;
    private final CalendarEventAttendeeRepository calendarEventAttendeeRepository;
    private final CalendarEventMapper eventMapper;
    private final MeetingService meetingService;



    @GetMapping("/vote/{voteId}")
    public void vote(@PathVariable Long voteId) {
        meetingService.markSuccessVote(voteId);

    }

    @PostMapping("/schedule")
    public ResponseEntity<ScheduleMeetingResponse> scheduleMeeting(
            @RequestParam UUID calendarId,

            @RequestBody ScheduleMeetingRequest request) {

        Calendar calendar = calendarService.getCalendarById(calendarId).orElseThrow();
        var periods = meetingService.organizeMeetingPoll(request.getTitle(), request.getHours(), request.getOrganizerId(),
                calendar, request.getAttendees(), request.getStartDate(), request.getEndDate());

        ScheduleMeetingResponse response = new ScheduleMeetingResponse();
        response.setResult("Success");
        response.setSlots(periods);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public void createCalendarEvent(@RequestBody CalendarEventCreateRequest calendarEventCreateRequest) {
        calendarEventService.createEvent(eventMapper.toEntity(calendarEventCreateRequest));
    }

    @GetMapping
    public List<CalendarEventResponse> getEventsForUser(
            @RequestParam UUID calendarId,
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "UTC") String timezone
    ) {
        // Валидация часового пояса
        ZoneId zone = ZoneId.of(timezone);


        // Получаем события (без обогащения)
        List<CalendarEvent> events = calendarEventService.findByCalendarAndPeriodAndAttendeeList(
                calendarId, LocalDateTime.ofInstant(start, zone),
                LocalDateTime.ofInstant(end, zone),
                List.of(userId));
        return events.stream()
                .map( calendarEvent -> toEventDto(calendarEvent, timezone))
                .toList();
    }

    private final UserServiceClient  userServiceClient;
    private final CalendarEventMapper calendarEventMapper;
    private CalendarEventResponse toEventDto(CalendarEvent calendarEvent, String timezone) {
        var attendees = calendarEventAttendeeRepository.findByCalendarEvent(calendarEvent).stream()
                .map(CalendarEventAttendee::getUserId).collect(Collectors.toSet());
        var usersMap = userServiceClient.getUsersByIds(attendees);
        var dto = calendarEventMapper.toResponse(calendarEvent, timezone);
        dto.setAttendees(new ArrayList<>(usersMap.values()));
        return dto;
    }
}