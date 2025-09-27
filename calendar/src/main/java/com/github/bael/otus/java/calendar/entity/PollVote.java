package com.github.bael.otus.java.calendar.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Table("poll_votes")
@Entity
public class PollVote {

    @Id
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
    private ZonedDateTime votedAt;
}