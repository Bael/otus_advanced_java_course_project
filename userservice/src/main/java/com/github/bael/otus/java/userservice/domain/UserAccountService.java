package com.github.bael.otus.java.userservice.domain;

import com.github.bael.otus.java.userservice.data.ReactiveUserAccountRepository;
import com.github.bael.otus.java.userservice.entity.UserAccount;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountService {

    private final ReactiveUserAccountRepository userAccountRepository;
//    private final PasswordEncoder passwordEncoder;

    private final RateLimiterRegistry rateLimiterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final BulkheadRegistry bulkheadRegistry;


    public Flux<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }

    // Способ 2: С аннотациями + реактивный fallback
    @RateLimiter(name = "userService", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "userService", fallbackMethod = "circuitBreakerFallback")
    @Retry(name = "userService", fallbackMethod = "retryFallback")
    @Bulkhead(name = "userService", fallbackMethod = "bulkheadFallback")
    public Flux<UserAccount> findByIds(Set<UUID> ids) {
        return userAccountRepository.findAllById(ids);
    }

    // Fallback методы должны возвращать тот же тип (Flux)
    public Flux<UserAccount> rateLimitFallback(Set<UUID> ids, RequestNotPermitted ex) {
        return Flux.error(new RuntimeException("Rate limit exceeded: " + ex.getMessage()));
    }

    public Flux<UserAccount> circuitBreakerFallback(Set<UUID> ids, CallNotPermittedException ex) {
        return Flux.error(new RuntimeException("Circuit breaker open: " + ex.getMessage()));
    }

    public Flux<UserAccount> retryFallback(Set<UUID> ids, Exception ex) {
        return Flux.error(new RuntimeException("All retry attempts failed: " + ex.getMessage()));
    }

    public Flux<UserAccount> bulkheadFallback(Set<UUID> ids, BulkheadFullException ex) {
        return Flux.error(new RuntimeException("Bulkhead full: " + ex.getMessage()));
    }

//    @RateLimiter(name = "userService", fallbackMethod = "rateLimitFallback")
//    @CircuitBreaker(name = "userService", fallbackMethod = "circuitBreakerFallback")
//    @Retry(name = "userService", fallbackMethod = "retryFallback")
//    @Bulkhead(name = "userService", fallbackMethod = "bulkheadFallback")
//    public Flux<UserAccount> findByIds(Set<UUID> ids) {
//
//        return userAccountRepository.findAllById(ids);
//    }
//
//    // Fallback методы
//    public Flux<UserAccount> rateLimitFallback(String id, RequestNotPermitted ex) {
//        return Flux.error(new RuntimeException("Rate limit exceeded: " + ex.getMessage()));
//    }
//
//    public Flux<UserAccount> circuitBreakerFallback(String id, CallNotPermittedException ex) {
//        return Flux.error(new RuntimeException("Circuit breaker open: " + ex.getMessage()));
//    }
//
//    public Flux<UserAccount> retryFallback(String id, Exception ex) {
//        return Flux.error(new RuntimeException("All retry attempts failed: " + ex.getMessage()));
//    }
//
//    public Flux<UserAccount> bulkheadFallback(String id, BulkheadFullException ex) {
//        return Flux.error(new RuntimeException("Bulkhead full: " + ex.getMessage()));
//    }


    public Mono<UserAccount> findById(UUID id) {
        return userAccountRepository.findById(id);
    }

    public Mono<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    public Mono<UserAccount> createUser(UserAccount userAccount, String rawPassword) {
        log.info("Creating user {}", userAccount);

//        userAccount.setPasswordHash(passwordEncoder.encode(rawPassword));
        userAccount.setPasswordHash(rawPassword);
        userAccount.setCreatedAt(Instant.now());
//        userAccount.setUpdatedAt(LocalDateTime.now());
        userAccount.setEnabled(true);

        var user = userAccountRepository.save(userAccount);
        log.info("User {} created" , userAccount);
        return user;
    }

    public Mono<UserAccount> updateUser(UserAccount userAccount) {
//        userAccount.setUpdatedAt(LocalDateTime.now());
        return userAccountRepository.save(userAccount);
    }

    public Mono<Void> deleteById(UUID id) {
        return userAccountRepository.deleteById(id);
    }

    public Mono<Boolean> existsByUsername(String username) {
        return userAccountRepository.existsByUsername(username);
    }

    public Mono<Boolean> existsByEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    public Mono<UserAccount> findByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }


}