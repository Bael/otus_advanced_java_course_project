package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;


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
@Entity
public class CalendarWeekdayPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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