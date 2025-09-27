package com.github.bael.otus.java.calendar.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "calendars")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private UUID ownerId;

    @Column(length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column
    private String color;

    @Column
    private Boolean isPublic = false;

    @Column
    private Instant createdAt;


}