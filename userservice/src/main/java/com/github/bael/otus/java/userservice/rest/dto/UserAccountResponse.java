package com.github.bael.otus.java.userservice.rest.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class UserAccountResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String timezone;
    private boolean enabled;
    private Instant createdAt;
}