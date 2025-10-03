package com.github.bael.otus.java.userservice.rest.controller;

import com.github.bael.otus.java.userservice.domain.UserAccountService;
import com.github.bael.otus.java.userservice.entity.UserAccount;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountCreateRequest;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountResponse;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountUpdateRequest;

import com.github.bael.otus.java.userservice.rest.mapper.UserAccountMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Account Management", description = "Reactive APIs for managing user accounts")
@Slf4j
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final UserAccountMapper userAccountMapper;

    @Operation(summary = "Get all users")
    @GetMapping
    public Flux<UserAccountResponse> getAllUsers() {
        return userAccountService.findAll()
                .map(userAccountMapper::toResponse);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserAccountResponse>> getUserById(@PathVariable UUID id) {
        return userAccountService.findById(id)
                .map(userAccountMapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    private final RateLimiterRegistry rateLimiterRegistry;


    //        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("userService");
//
//        return Mono.fromCallable(() -> rateLimiter.acquirePermission())
//                .flatMap(permission -> {
//                    if (permission) {
//                        return userAccountService.findByIds(ids).collectList()
//                                .map(ResponseEntity::ok);
//                    } else {
//                        return Mono.just(ResponseEntity.status(429).build());
//                    }
//                });

    @Operation(summary = "Get users by ID list")
    @PostMapping("/")
    public Mono<ResponseEntity<List<UserAccountResponse>>> getUsersByIds(@RequestBody Set<UUID> ids) {

        return userAccountService.findByIds(ids)
                .map(userAccountMapper::toResponse)
                .collectList().map(ResponseEntity::ok);

    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public Mono<ResponseEntity<UserAccountResponse>> createUser(@Valid @RequestBody UserAccountCreateRequest request) {
        UserAccount userAccount = userAccountMapper.toEntity(request);

        return userAccountService.existsByUsername(request.getUsername())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return userAccountService.createUser(userAccount, request.getPassword())
                            .map(userAccount1 -> {
                                log.info("created user!!!! {}", userAccount1);
                                return userAccount1;
                            })
                            .map(userAccountMapper::toResponse)

                            .map(response -> ResponseEntity
                                    .created(URI.create("/api/users/" + response.getId()))
                                    .body(response));
                });
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserAccountResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserAccountUpdateRequest request) {

        return userAccountService.findById(id)
                .flatMap(existingUser -> {
                    userAccountMapper.updateEntityFromRequest(request, existingUser);
                    return userAccountService.updateUser(existingUser);
                })
                .map(userAccountMapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable UUID id) {
        return userAccountService.findById(id)
                .flatMap(user -> userAccountService.deleteById(id)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user by username")
    @GetMapping("/username/{username}")
    public Mono<ResponseEntity<UserAccountResponse>> getUserByUsername(@PathVariable String username) {
        return userAccountService.findByUsername(username)
                .map(userAccountMapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}