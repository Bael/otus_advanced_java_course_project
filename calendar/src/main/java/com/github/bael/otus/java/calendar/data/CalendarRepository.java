package com.github.bael.otus.java.calendar.data;

import com.github.bael.otus.java.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface CalendarRepository extends CrudRepository<Calendar, UUID> {

    List<Calendar> findByOwnerId(UUID ownerId);

    @Query(value = "SELECT * FROM calendars WHERE owner_id = :ownerId AND is_public = true", nativeQuery = true)
    List<Calendar> findPublicCalendarsByOwnerId(@Param("ownerId") UUID ownerId);
}