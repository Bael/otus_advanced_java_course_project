package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Table("poll_attendees")
public class PollAttendee {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private SchedulingPoll poll;

    @Column
    private UUID userId;

    @Column
    private ZonedDateTime createdAt;
}