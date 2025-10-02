package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Table(name = "poll_attendees")
@Entity
public class PollAttendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private SchedulingPoll poll;

    @Column
    private UUID userId;

    @Column
    private Instant createdAt;
}