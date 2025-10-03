package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.*;
import com.github.bael.otus.java.calendar.entity.*;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.interop.notification.client.NotificationServiceClient;
import com.github.bael.otus.java.calendar.interop.notification.dto.EventNotificationRequest;
import com.github.bael.otus.java.calendar.interop.notification.dto.RecipientDto;
import com.github.bael.otus.java.calendar.interop.user.UserInfo;
import com.github.bael.otus.java.calendar.interop.user.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

    private final AvailabilityService availabilityService;
    private final PollAttendeeRepository pollAttendeeRepository;
    private final PollTimeSlotRepository pollTimeSlotRepository;
    private final SchedulingPollRepository schedulingPollRepository;
    private final CalendarEventAttendeeRepository calendarEventAttendeeRepository;
    private final CalendarEventRepository calendarEventRepository;

    @Transactional
    public List<Range<LocalDateTime>> organizeMeetingPoll(String title, int hours, UUID organizerId,
                                    Calendar calendar, List<UUID> attendees,
                                LocalDate startDate, LocalDate endDate) {

        SchedulingPoll schedulingPoll = new SchedulingPoll();
        schedulingPoll.setTitle(title);
        schedulingPoll.setCalendar(calendar);
        // 30 minutes
        schedulingPoll.setDeadline(Instant.now().plusSeconds(3 * 60));
        schedulingPoll.setCreatedAt(Instant.now());
        schedulingPoll.setStatus(SchedulingPollStatus.OPEN);
        schedulingPoll.setDurationMinutes(hours * 60);
        schedulingPoll.setStartTime(startDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        schedulingPoll.setFinishTime(endDate.atStartOfDay().toInstant(ZoneOffset.UTC));

        schedulingPoll.setOrganizerId(organizerId);

        schedulingPollRepository.save(schedulingPoll);

        for (UUID attendeeId : attendees) {
            PollAttendee attendee = new PollAttendee();
            attendee.setPoll(schedulingPoll);
            attendee.setUserId(attendeeId);
            attendee.setCreatedAt(Instant.now());
            pollAttendeeRepository.save(attendee);
        }

        List<Range<LocalDateTime>> availablePeriodsUTC =
                availabilityService.getAvailablePeriodsUTC(calendar, organizerId, startDate, endDate, attendees,
                        Duration.ofHours(hours));

        List<PollTimeSlot> pollTimeSlots = new ArrayList<>();
        for (var entry : availablePeriodsUTC) {
            PollTimeSlot timeSlot = new PollTimeSlot();
            timeSlot.setPoll(schedulingPoll);
            timeSlot.setStartTime(entry.getLowerBound().getValue().get().toInstant(ZoneOffset.UTC));
            timeSlot.setEndTime(entry.getUpperBound().getValue().get().toInstant(ZoneOffset.UTC));
            timeSlot.setCreatedAt(Instant.now());
            pollTimeSlots.add(timeSlot);
        }
        pollTimeSlotRepository.saveAll(pollTimeSlots);


        generateVoteLinks(pollTimeSlots, attendees, schedulingPoll);

        return availablePeriodsUTC;

    }


    private final UserServiceClient userServiceClient;

//    private final EmailService emailService;
    private final PollVoteRepository pollVoteRepository;
    private final NotificationServiceClient notificationServiceClient;

    @Transactional
    protected void generateVoteLinks(List<PollTimeSlot> pollTimeSlots, List<UUID> attendees, SchedulingPoll schedulingPoll) {
        Map<UUID, UserInfo> usersByIds = userServiceClient.getUsersByIds(new HashSet<>(attendees));

        for (var entry : usersByIds.entrySet()) {
//
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("Выберите слот для голосования <br> ").append("\n");

            List<PollVote> userVotes = new ArrayList<>();
            for (var slot : pollTimeSlots) {
                PollVote vote = new PollVote();
                vote.setPoll(schedulingPoll);
                vote.setUserId(entry.getKey());
                vote.setTimeSlot(slot);
                // status is null
                userVotes.add(vote);
//
//
//                String link = "http://localhost:12102/api/v1/calendar/events/vote/" + vote.getId();
//                stringBuilder.append("<UNK> <UNK> <UNK> <UNK> <br> <a href="+ link +">" + slot.getStartTime() + " " + slot.getEndTime() +  "<a/>").append("\n");
            }
            pollVoteRepository.saveAll(userVotes);
            notificationServiceClient.sendVoteNotification(entry.getValue(), userVotes, schedulingPoll);

//            emailService.sendSimpleMessage(entry.getValue().getEmail(), "Внимание! Назначена встреча!", stringBuilder.toString());


        }

    }



    @Transactional
    public String markSuccessVote(Long voteId) {
        var vote = pollVoteRepository.findById(voteId).orElse(null);
        if (vote == null) {
            return "<UNK> <UNK> Vote does not exits <UNK>";
        }

        if (vote.getPoll().getDeadline().isBefore(Instant.now())) {
            return "Голосование просрочено";
        }

        if (vote.getVote() != null) {
            return "Вы уже голосовали!";
        }
        vote.setVote(PollVoteStatus.APPROVED);
        pollVoteRepository.save(vote);
        checkSchedulingPollStatus(vote.getPoll());
        return "Успешно проголосовали!";
    }

    private void checkSchedulingPollStatus(SchedulingPoll poll) {
        var votes = pollVoteRepository.findByPollId(poll.getId());

        var users = pollAttendeeRepository.findByPoll_Id(poll.getId())
                .stream()
                .map(PollAttendee::getUserId).toList();

        var votedUsersSet = votes.stream()
                .filter(pollVote -> pollVote.getVote() == PollVoteStatus.APPROVED).map(PollVote::getUserId)
                .collect(Collectors.toSet());
        var usersToVote = new HashSet<>(users);
        usersToVote.removeAll(votedUsersSet);
        boolean allVoted = usersToVote.isEmpty();
//
//        var allVoted = votes.stream()
//                .allMatch(pollVote -> pollVote.getVote() == PollVoteStatus.APPROVED);


        var map = votes.stream().collect(Collectors.groupingBy(PollVote::getTimeSlot, Collectors.counting()));
        long max = -1;
        PollTimeSlot maxSlot = null;
        for (var entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxSlot = entry.getKey();
            }
        }

        if (allVoted) {
            poll.setStatus(SchedulingPollStatus.SUCCEEDED);
            // to do create event
            CalendarEvent event = new CalendarEvent();
            event.setTitle(poll.getTitle());
            event.setStatus(CalendarEventStatus.CONFIRMED);
            event.setStartTime(maxSlot.getStartTime());
            event.setEndTime(maxSlot.getEndTime());
            event.setCalendar(poll.getCalendar());
            event.setUserId(poll.getOrganizerId());
            calendarEventRepository.save(event);
            poll.setCreatedEventId(event);
            schedulingPollRepository.save(poll);

//            var users = pollAttendeeRepository.findByPoll_Id(poll.getId())
//                    .stream()
//                    .map(PollAttendee::getUserId).toList();
//
            var usersMap = userServiceClient.getUsersByIds(new HashSet<>(users));
            log.info("Нужно оповестить пользователей {} об успешном согласовании встречи {}", usersMap.entrySet(),
                    poll.getTitle());

            for (var userId : users) {
                CalendarEventAttendee attendee = new CalendarEventAttendee();
                attendee.setCalendarEvent(event);
                attendee.setUserId(userId);
                calendarEventAttendeeRepository.save(attendee);

                var userInfo = usersMap.get(userId);
                RecipientDto recipient = new RecipientDto(userInfo.getUserId(), userInfo.getEmail(), userInfo.fullname());
                EventNotificationRequest request = new EventNotificationRequest(recipient, "Успешно согласована встреча!"
                        , Instant.now(), "Успешно согласована встреча " + poll.getTitle()
                        + " на время   " + maxSlot.getStartTime() +" " + maxSlot.getEndTime());
                log.info("Оповещаем пользователя {} ", userInfo.getEmail());
                notificationServiceClient.sendEventNotification(request);
            }



        }


    }
}
