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

@Repository
public interface CalendarEventRepository extends CrudRepository<CalendarEvent, UUID> {

    List<CalendarEvent> findByCalendarId(UUID calendarId);

    @Query(value = "SELECT * FROM calendar_events  WHERE calendar_id = :calendarId AND start_time between :start AND :end", nativeQuery = true)
    List<CalendarEvent> findByCalendarIdAndPeriod(
            @Param("calendarId") UUID calendarId,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );

    @Query(value = "SELECT ce.* FROM calendar_events ce " +
            "JOIN calendars c ON ce.calendar_id = c.id " +
            "WHERE c.owner_id = :ownerId AND ce.start_time between :start AND :end", nativeQuery = true)
    List<CalendarEvent> findByOwnerIdAndPeriod(
            @Param("ownerId") UUID ownerId,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );
}