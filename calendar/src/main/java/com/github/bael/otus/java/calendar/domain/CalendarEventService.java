package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarEventAttendeeRepository;
import com.github.bael.otus.java.calendar.data.CalendarEventRepository;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.entity.CalendarEventAttendee;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarEventService {

    private final CalendarEventRepository eventRepository;

    public List<CalendarEvent> getEventsByCalendarId(UUID calendarId) {
        return eventRepository.findByCalendarId(calendarId);
    }

    public List<CalendarEvent> getEventsByCalendarIdAndPeriod(UUID calendarId, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByCalendarIdAndPeriod(calendarId, start, end);
    }


    @RateLimiter(name = "calendarService", fallbackMethod = "rateLimitFallback")
    public List<CalendarEvent> findByCalendarAndPeriodAndAttendeeList(UUID calendarId, LocalDateTime start, LocalDateTime end, List<UUID> attendees) {
        return eventRepository.findByCalendarAndPeriodAndAttendeeList(calendarId, start, end, attendees);
    }

    public List<CalendarEvent> rateLimitFallback(UUID calendarId, LocalDateTime start, LocalDateTime end, List<UUID> attendees) {
        throw new RuntimeException("Rate limit fallback");
    }



    public Optional<CalendarEvent> getEventById(UUID id) {
        return eventRepository.findById(id);
    }

    private final CalendarEventAttendeeRepository attendeeRepository;

    @Transactional
    public CalendarEvent createEvent(CalendarEvent event) {
        eventRepository.save(event);
        CalendarEventAttendee attendee = new CalendarEventAttendee();
        attendee.setCalendarEvent(event);
        attendee.setUserId(event.getUserId());
        attendeeRepository.save(attendee);

        return event;
    }

    public CalendarEvent updateEvent(CalendarEvent event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }
}