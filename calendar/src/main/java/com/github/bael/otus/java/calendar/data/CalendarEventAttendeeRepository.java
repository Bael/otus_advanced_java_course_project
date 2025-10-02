package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.entity.CalendarEventAttendee;
import com.github.bael.otus.java.calendar.entity.PollAttendee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CalendarEventAttendeeRepository extends CrudRepository<CalendarEventAttendee, Long> {

    List<CalendarEventAttendee> findByCalendarEvent(CalendarEvent calendarEvent);

}