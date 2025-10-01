package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarRepository;
import com.github.bael.otus.java.calendar.data.PollAttendeeRepository;
import com.github.bael.otus.java.calendar.data.PollTimeSlotRepository;
import com.github.bael.otus.java.calendar.data.SchedulingPollRepository;
import com.github.bael.otus.java.calendar.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final AvailabilityService availabilityService;
    private final PollAttendeeRepository pollAttendeeRepository;
    private final PollTimeSlotRepository pollTimeSlotRepository;
    private final SchedulingPollRepository schedulingPollRepository;

    @Transactional
    public void organizeMeetingPoll(String title, int hours, UUID organizerId,
                                    Calendar calendar, List<UUID> attendees,
                                LocalDate startDate, LocalDate endDate) {

        SchedulingPoll schedulingPoll = new SchedulingPoll();
        schedulingPoll.setTitle(title);
        schedulingPoll.setDeadline(Instant.now().plusSeconds(2 * 24 * 3600));
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

        Map<LocalDate, List<Period>> availablePeriodsUTC =
                availabilityService.getAvailablePeriodsUTC(calendar, organizerId, startDate, endDate, attendees);
        for (var entry : availablePeriodsUTC.entrySet()) {
            LocalDate date = entry.getKey();
            List<Period> periods = entry.getValue();
            for (Period period : periods) {
                PollTimeSlot timeSlot = new PollTimeSlot();
                timeSlot.setPoll(schedulingPoll);
                timeSlot.setStartTime(LocalDate.now().atTime(period.getStartHour(), 0, 0).toInstant(ZoneOffset.UTC));

                timeSlot.setEndTime(LocalDate.now().atTime(period.getFinishHour(), 0, 0).toInstant(ZoneOffset.UTC));
                timeSlot.setCreatedAt(Instant.now());
                pollTimeSlotRepository.save(timeSlot);
            }
        }

    }

    @Transactional
    public void voteForSlot(UUID userId, Long slotId, PollVoteStatus pollVoteStatus) {
        // todo create slot
        // todo check voting is over



    }
}
