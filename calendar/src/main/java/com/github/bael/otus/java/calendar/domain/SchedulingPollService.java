package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.PollAttendeeRepository;
import com.github.bael.otus.java.calendar.data.SchedulingPollRepository;
import com.github.bael.otus.java.calendar.entity.PollAttendee;
import com.github.bael.otus.java.calendar.entity.SchedulingPoll;
import com.github.bael.otus.java.calendar.entity.SchedulingPollStatus;
import com.github.bael.otus.java.calendar.interop.notification.client.NotificationServiceClient;
import com.github.bael.otus.java.calendar.interop.notification.dto.EventNotificationRequest;
import com.github.bael.otus.java.calendar.interop.notification.dto.RecipientDto;
import com.github.bael.otus.java.calendar.interop.user.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingPollService {

    private final SchedulingPollRepository pollRepository;
    private final PollAttendeeRepository pollAttendeeRepository;
    private final NotificationServiceClient notificationServiceClient;
    private final UserServiceClient userServiceClient;

    @Scheduled(fixedRate = 60_000)
    public void cancelExpiresPolls() {
        pollRepository.findAllByDeadlineAfterAndStatus(Instant.now(), SchedulingPollStatus.OPEN)
                .forEach(this::exprirePoll);
    }

    private void exprirePoll(SchedulingPoll schedulingPoll) {
        schedulingPoll.setStatus(SchedulingPollStatus.EXPIRED);
        pollRepository.save(schedulingPoll);

        String message = "Согласование встречи  " + schedulingPoll.getTitle() + " отменено! ";

        Set<UUID> userIds = pollAttendeeRepository.findByPoll_Id(schedulingPoll.getId()).stream().map(PollAttendee::getUserId).collect(Collectors.toSet());
        var usersMap = userServiceClient.getUsersByIds(userIds);
        log.info("Нужно оповестить пользователей {} об отмене согласования встречи {}", usersMap.entrySet(), schedulingPoll.getTitle());
        for (var entry : usersMap.entrySet()) {
            var userInfo = entry.getValue();
            RecipientDto recipient = new RecipientDto(userInfo.getUserId(), userInfo.getEmail(), userInfo.fullname());
            EventNotificationRequest request = new EventNotificationRequest(recipient, "EXPIRED POLL", Instant.now(), message);
            log.info("Оповещаем пользователя {} ", userInfo.getEmail());
            notificationServiceClient.sendEventNotification(request);
        }
    }
}
