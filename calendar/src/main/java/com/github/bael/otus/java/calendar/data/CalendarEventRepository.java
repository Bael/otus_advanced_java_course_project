package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.CalendarEvent;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface CalendarEventRepository extends CrudRepository<CalendarEvent, UUID> {

    List<CalendarEvent> findByCalendarId(UUID calendarId);

    @Query(value = "SELECT * FROM calendar_events  WHERE calendar_id = :calendarId AND start_time between :start AND :end", nativeQuery = true)
    List<CalendarEvent> findByCalendarIdAndPeriod(
            @Param("calendarId") UUID calendarId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = "SELECT ce.* FROM calendar_events ce " +
            "JOIN calendars c ON ce.calendar_id = c.id " +
            "JOIN calendar_event_attendee attendee ON ce.id = attendee.calendar_event_id " +
            "WHERE c.id = :calendar_id AND ce.start_time between :start AND :end "+
            " and attendee.user_id in (:attendeeList)"
            , nativeQuery = true)
    List<CalendarEvent> findByCalendarAndPeriodAndAttendeeList(
            @Param("calendar_id") UUID calendar_id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("attendeeList") List<UUID> attendeeList
            );
}