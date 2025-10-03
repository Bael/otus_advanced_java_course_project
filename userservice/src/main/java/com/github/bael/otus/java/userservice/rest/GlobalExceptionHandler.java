package com.github.bael.otus.java.userservice.rest;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> handleRateLimitException(RequestNotPermitted ex) {
        return ResponseEntity.status(429)
                .header("X-Rate-Limit-Retry-After", "1")
                .body("Rate limit exceeded. Please try again later.");
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<String> handleCircuitBreakerException(CallNotPermittedException ex) {
        return ResponseEntity.status(503)
                .body("Service temporarily unavailable. Please try again later.");
    }

    @ExceptionHandler(BulkheadFullException.class)
    public ResponseEntity<String> handleBulkheadException(BulkheadFullException ex) {
        return ResponseEntity.status(503)
                .body("Too many concurrent requests. Please try again later.");
    }
}