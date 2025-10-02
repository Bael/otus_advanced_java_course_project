package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * участник события календаря
 */
@Data
@Table(name = "calendar_event_attendee")
@Entity
public class CalendarEventAttendee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_event_id")
    private CalendarEvent calendarEvent;

    @Column(name = "user_id")
    private UUID userId;




}