package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarWeekdayPeriod;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CalendarWeekdayPeriodRepository extends CrudRepository<CalendarWeekdayPeriod, Long> {
    List<CalendarWeekdayPeriod> findByCalendarAndUserId(Calendar calendar, UUID userId);

    List<CalendarWeekdayPeriod> findByCalendar(Calendar calendar);
}
