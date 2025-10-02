package com.github.bael.otus.java.calendar.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * голосование
 */
@Data
@Table(name = "poll_votes")
@Entity
public class PollVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private SchedulingPoll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id")
    private PollTimeSlot timeSlot;

    @Column
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private PollVoteStatus vote; // APPROVE, DENY

    @Column
    private Instant votedAt;
}