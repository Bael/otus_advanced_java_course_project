package com.github.bael.otus.java.userservice.rest.controller;

import com.github.bael.otus.java.userservice.domain.UserAccountService;
import com.github.bael.otus.java.userservice.entity.UserAccount;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountCreateRequest;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountResponse;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountUpdateRequest;
import com.github.bael.otus.java.userservice.rest.mapper.UserAccountMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/queries/users")
@RequiredArgsConstructor
@Tag(name = "User Account Queries", description = "Reactive APIs for managing user accounts")
@Slf4j
public class UserAccountQueriesController {

    private final UserAccountService userAccountService;
    private final UserAccountMapper userAccountMapper;

    @Operation(summary = "Get all users")
    @GetMapping
    public Flux<UserAccountResponse> getAllUsers() {
        return userAccountService.findAll()
                .map(userAccountMapper::toResponse);
    }

    @Operation(summary = "Get all user names")
    @GetMapping(value = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> getAllUserNames() {
        return userAccountService.findAll()
                .map(UserAccount::getFio).collect(Collectors.toList());
    }

    @Operation(summary = "Get all non empty user emails")
    @GetMapping(value = "/emails", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllUserEmails() {
        return userAccountService.findAll()
                .filter(userAccount -> !userAccount.getEmail().isEmpty())
                .map(UserAccount::getEmail);
    }


}