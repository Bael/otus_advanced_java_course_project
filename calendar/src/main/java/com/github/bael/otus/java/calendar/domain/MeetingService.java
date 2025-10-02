package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.*;
import com.github.bael.otus.java.calendar.entity.*;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.interop.user.UserInfo;
import com.github.bael.otus.java.calendar.interop.user.UserServiceClient;
import javafx.scene.canvas.GraphicsContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
        schedulingPoll.setDeadline(Instant.now().plusSeconds(30 * 60));
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

    private final EmailService emailService;
    private final PollVoteRepository pollVoteRepository;
    @Transactional
    protected void generateVoteLinks(List<PollTimeSlot> pollTimeSlots, List<UUID> attendees, SchedulingPoll schedulingPoll) {
        Map<UUID, UserInfo> usersByIds = userServiceClient.getUsersByIds(new HashSet<>(attendees));


        for (var entry : usersByIds.entrySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Выберите слот для голосования <br> ").append("\n");

            for (var slot : pollTimeSlots) {
                PollVote vote = new PollVote();
                vote.setPoll(schedulingPoll);
                vote.setUserId(entry.getKey());
                vote.setTimeSlot(slot);
                // status is null
                pollVoteRepository.save(vote);
                String link = "http://localhost:12102/api/v1/calendar/events/vote/" + vote.getId();
                stringBuilder.append("<UNK> <UNK> <UNK> <UNK> <br> <a href="+ link +">" + slot.getStartTime() + " " + slot.getEndTime() +  "<a/>").append("\n");
            }

            emailService.sendSimpleMessage(entry.getValue().getEmail(), "Внимание! Назначена встреча!", stringBuilder.toString());


        }

    }

    @Transactional
    public void voteForSlot(UUID userId, Long slotId, PollVoteStatus pollVoteStatus) {
        // todo create slot
        // todo check voting is over



    }

    public String markSuccessVote(Long voteId) {
        var vote = pollVoteRepository.findById(voteId).orElse(null);
        if (vote == null) {
            return "<UNK> <UNK> Vote does not exits <UNK>";
        }
        vote.setVote(PollVoteStatus.APPROVED);
        pollVoteRepository.save(vote);
        checkSchedulingPollStatus(vote.getPoll());
        return "Успешно проголосовали!";
    }

    private void checkSchedulingPollStatus(SchedulingPoll poll) {
        var votes = pollVoteRepository.findByPollId(poll.getId());

        var allVoted = votes.stream()
                .allMatch(pollVote -> pollVote.getVote() == PollVoteStatus.APPROVED);

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

            var users = pollAttendeeRepository.findByPoll_Id(poll.getId())
                    .stream()
                    .map(PollAttendee::getUserId).toList();
            for (var userId : users) {
                CalendarEventAttendee attendee = new CalendarEventAttendee();
                attendee.setCalendarEvent(event);
                attendee.setUserId(userId);
                calendarEventAttendeeRepository.save(attendee);
            }

            event.setUserId(poll.getOrganizerId());
        }


    }
}
