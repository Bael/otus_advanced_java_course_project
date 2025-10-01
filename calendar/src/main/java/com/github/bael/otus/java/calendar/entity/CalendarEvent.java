package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Table(name = "calendar_events")
@Entity
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column
    private Instant startTime;

    @Column
    private Instant endTime;

//    @Column(name = "all_day")
//    private Boolean allDay;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    private CalendarEventStatus status;

    /**
     * повторяющееся событие?
     */
    @Column
    private String scheduleCronExpression;

    @JoinColumn(name = "parent_event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CalendarEvent parentEvent;


}