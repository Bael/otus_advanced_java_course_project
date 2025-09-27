package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
public class SchedulingPoll {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     organizer user */
    @Column
    private UUID organizerId;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Integer durationMinutes;

    @Column
    private String timezone;

    @Enumerated(EnumType.STRING)
    private SchedulingPollStatus status; // OPEN, CLOSED, CANCELLED

    @Column
    private ZonedDateTime deadline;

    @Column
    private ZonedDateTime finalStartTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_event_id")
    private CalendarEvent createdEventId;

    @Column
    private ZonedDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

}