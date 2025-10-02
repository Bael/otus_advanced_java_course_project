package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarWeekdayPeriod;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarWeekdayPeriodRepository extends CrudRepository<CalendarWeekdayPeriod, Long> {
    List<CalendarWeekdayPeriod> findByCalendarAndUserId(Calendar calendar, UUID userId);

    List<CalendarWeekdayPeriod> findByCalendar(Calendar calendar);

    void deleteAllByUserId(UUID userId);
    void deleteAllByCalendar_IdAndUserId(UUID calendarId, UUID userId);
}
