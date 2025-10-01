package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * открытые периоды в календаре пользователя (UTC)
 */
@Data
@Table(name = "calendar_weekday_periods")
public class CalendarWeekdayPeriod {
    @Id
    private Long id;

    @Column
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id" )
    private Calendar calendar;

    @Enumerated(EnumType.STRING)
    private DayOfWeek weekDay;

    @Column
    private LocalTime start;

    @Column
    private LocalTime finish;

}