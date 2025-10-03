package com.github.bael.otus.java.rest.dto;

import java.util.UUID;

public record RecipientDto(
        UUID userId,
        String email,
        String fullName
) {}