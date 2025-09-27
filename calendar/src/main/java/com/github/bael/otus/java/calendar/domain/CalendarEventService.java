package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarEventRepository;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;
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

    public List<CalendarEvent> getEventsByCalendarIdAndPeriod(UUID calendarId, ZonedDateTime start, ZonedDateTime end) {
        return eventRepository.findByCalendarIdAndPeriod(calendarId, start, end);
    }

    public List<CalendarEvent> getEventsByOwnerIdAndPeriod(UUID ownerId, ZonedDateTime start, ZonedDateTime end) {
        return eventRepository.findByOwnerIdAndPeriod(ownerId, start, end);
    }

    public Optional<CalendarEvent> getEventById(UUID id) {
        return eventRepository.findById(id);
    }

    public CalendarEvent createEvent(CalendarEvent event) {
        return eventRepository.save(event);
    }

    public CalendarEvent updateEvent(CalendarEvent event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }
}