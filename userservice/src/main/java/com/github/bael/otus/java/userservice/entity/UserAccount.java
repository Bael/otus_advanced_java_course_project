package com.github.bael.otus.java.userservice.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
//import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "user_accounts")
@Getter
@Setter
public class UserAccount {

    @Id

    private UUID id;

    @Column
//            (unique = true, nullable = false)
    private String username;

    @Column //(unique = true, nullable = false)
    private String email;

    @Column //(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column //(name = "first_name")
    private String firstName;

    @Column //(name = "last_name")
    private String lastName;

    private String timezone;

    @Column //(name = "enabled")
    private Boolean enabled;

//    @CreationTimestamp
    @Column //(name = "created_at", updatable = false)
    private Instant createdAt;


    public String getFio() {
        return getFirstName() + " " + getLastName();
    }
}
