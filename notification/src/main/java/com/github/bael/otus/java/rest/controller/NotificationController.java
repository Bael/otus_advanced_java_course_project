package com.github.bael.otus.java.rest.controller;

import com.github.bael.otus.java.domain.NotificationService;
import com.github.bael.otus.java.rest.dto.EventNotificationRequest;
import com.github.bael.otus.java.rest.dto.VotingNotificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Send voting and event notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Send voting notification to user")
    @PostMapping("/voting")
    public ResponseEntity<Void> sendVotingNotification(
            @RequestBody VotingNotificationRequest request
    ) {
        notificationService.sendVotingNotification(request);
        return ResponseEntity.accepted().build(); // 202 Accepted — асинхронная обработка
    }

    @Operation(summary = "Send general event notification to user")
    @PostMapping("/events")
    public ResponseEntity<Void> sendEventNotification(
            @RequestBody EventNotificationRequest request
    ) {
        notificationService.sendEventNotification(request);
        return ResponseEntity.accepted().build();
    }
}
