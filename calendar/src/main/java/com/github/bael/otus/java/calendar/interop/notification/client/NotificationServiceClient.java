package com.github.bael.otus.java.calendar.interop.notification.client;


import com.github.bael.otus.java.calendar.entity.PollVote;
import com.github.bael.otus.java.calendar.entity.SchedulingPoll;
import com.github.bael.otus.java.calendar.interop.notification.dto.EventNotificationRequest;
import com.github.bael.otus.java.calendar.interop.notification.dto.RecipientDto;
import com.github.bael.otus.java.calendar.interop.notification.dto.VotingNotificationRequest;
import com.github.bael.otus.java.calendar.interop.notification.dto.VotingSlotDto;
import com.github.bael.otus.java.calendar.interop.user.UserAccountResponse;
import com.github.bael.otus.java.calendar.interop.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NotificationServiceClient {

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final String calendarServiceUrl;

    public NotificationServiceClient(RestTemplateBuilder restTemplateBuilder,
                                     @Value("${notification-service.url:http://localhost:12103}") String notificationServiceUrl,
                                             @Value("${calendar-service.url:http://localhost:12102}") String calendarServiceUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.notificationServiceUrl = notificationServiceUrl;
        this.calendarServiceUrl = calendarServiceUrl;
    }

    /**
     * уведомляем о необходимости голосования
     */
    public void sendVoteNotification(UserInfo votingUser, List<PollVote> votes, SchedulingPoll schedulingPoll) {

        // Формируем URI
        String url = notificationServiceUrl + "/api/v1/notifications/voting";

        // Заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RecipientDto recipientDto = new RecipientDto(votingUser.getUserId(),
                votingUser.getEmail(),
                votingUser.getFirstName() + " " + votingUser.getLastName());
        var votesDtoList = votes.stream().map(
                pollVote -> new VotingSlotDto(pollVote.getTimeSlot().getStartTime(),
                        pollVote.getTimeSlot().getEndTime(),
                        calendarServiceUrl + "/api/v1/calendar/events/vote/" + pollVote.getId()
                        )).toList();
        VotingNotificationRequest votingRequest = new VotingNotificationRequest(recipientDto,
                schedulingPoll.getTitle(),
                "пожалуйста проголосуйте",
                votesDtoList
                );
        HttpEntity<VotingNotificationRequest> request = new HttpEntity<>(votingRequest, headers);

        try {
            ResponseEntity<Void> response = restTemplate
                    .exchange(url, HttpMethod.POST, request, Void.class);

            log.info("Статус оповещения пользователя {}", response.getStatusCode());
            if (!(response.getStatusCode() == HttpStatus.ACCEPTED)) {
                throw new RuntimeException("Notification service returned non-OK status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to send vote notify event: " + e.getMessage(), e);
        }
    }


    public void sendEventNotification(EventNotificationRequest request) {

        // Формируем URI
        String url = notificationServiceUrl + "/api/v1/notifications/events";

        // Заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EventNotificationRequest> httpRequest = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Void> response = restTemplate
                    .exchange(url, HttpMethod.POST, httpRequest, Void.class);
            log.info("Оповещаем пользователя {} ", request.recipient().email());

            if (!(response.getStatusCode() == HttpStatus.ACCEPTED)) {
                throw new RuntimeException("Notification service returned non-OK status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to send vote notify event: " + e.getMessage(), e);
        }

    }
}