package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarRepository;
import com.github.bael.otus.java.calendar.entity.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public List<Calendar> getUserCalendars(UUID ownerId) {
        return calendarRepository.findByOwnerId(ownerId);
    }

    public Optional<Calendar> getCalendarById(UUID id) {
        return calendarRepository.findById(id);
    }

    public Calendar createCalendar(Calendar calendar) {
        return calendarRepository.save(calendar);
    }

    public Calendar updateCalendar(Calendar calendar) {
        return calendarRepository.save(calendar);
    }

    public void deleteCalendar(UUID id) {
        calendarRepository.deleteById(id);
    }

    public boolean calendarExists(UUID id) {
        return calendarRepository.existsById(id);
    }
}