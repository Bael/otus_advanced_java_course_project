package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * время для выбора голосования
 */
@Data
@Entity
@Table(name = "poll_time_slots")
public class PollTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private SchedulingPoll poll;

    @Column
    private Instant startTime;

    @Column
    private Instant endTime;

    @Column
    private Instant createdAt;
}