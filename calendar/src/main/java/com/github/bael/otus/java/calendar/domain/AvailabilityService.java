package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarWeekdayPeriodRepository;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.entity.CalendarEventStatus;
import com.github.bael.otus.java.calendar.entity.CalendarWeekdayPeriod;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final CalendarService calendarService;
    private final CalendarEventService  calendarEventService;
    private final CalendarWeekdayPeriodRepository calendarWeekdayPeriodRepository;
    public Map<LocalDate, List<Period>> getAvailablePeriodsUTC(Calendar calendar, UUID organizer,
                                                               LocalDate startDate, LocalDate endDate,
                                                            List<UUID> attendees) {

        return Map.of(startDate.plusDays(1), List.of(Period.of(10, 11)),
                startDate.plusDays(2), List.of(Period.of(10, 11)),
                startDate.plusDays(3), List.of(Period.of(10, 11))
                );
//
//
//        HourTimeline hourTimeline = new HourTimeline(startDate, endDate);
//
//        List<CalendarWeekdayPeriod> byCalendar = calendarWeekdayPeriodRepository.findByCalendarAndUserId(calendar, organizer);
//        for(var period : byCalendar) {
//            hourTimeline.addOpenPeriod(period.getStart(), period.getFinish());
//        }
//
//
//
//
//                for (UUID id : attendees) {
//            List<Calendar> userCalendars = calendarService.getUserCalendars(id);
//
//            for (Calendar calendar : userCalendars) {
//                if (calendar.getCalendarType() == CalendarType.WORK) {
//
//
//                    ZoneId zone = getZoneFromCalendar(calendar);
//                    // to do add opened period
//                    List<CalendarWeekdayPeriod> byCalendar = calendarWeekdayPeriodRepository.findByCalendar(calendar);
//                    for(var period : byCalendar) {
//
//                    }
//                    ZonedDateTime start = ZonedDateTime.of(startDate.atStartOfDay(), zone);
//                    ZonedDateTime finish = ZonedDateTime.of(endDate.atStartOfDay().plusHours(24), zone);
//
//
//                    List<CalendarEvent> eventsByCalendarIdAndPeriod = calendarEventService.getEventsByCalendarIdAndPeriod(calendar.getId(), start, finish);
//                    for (CalendarEvent event : eventsByCalendarIdAndPeriod) {
//                        if (event.getStatus() == CalendarEventStatus.CONFIRMED) {
//                            LocalDateTime startEvent = event.getStartTime().toLocalDateTime();
//                            LocalDateTime endEvent = event.getEndTime().toLocalDateTime();
//                            hourTimeline.closePeriod(startEvent, endEvent);
//                        }
//                    }
//                }
//            }
//            userCalendars.stream()
//                    .filter(calendar -> calendar.getCalendarType() == CalendarType.WORK)
//                    .forEach(calendar -> {
//
//
//
//            });
//            // get calendar by type
//            // get constraints/ open periods by type
//            // get events by calendar/ add as closed dates
//            // intersect
//
//        }
//
//
//        return null;
//

    }

//    private ZoneId getZoneFromCalendar(Calendar calendar) {
//       return ZoneId.of(calendar.getTimezone());
//    }

//    public List<Range<ZonedDateTime>> getAvailablePeriodsForAttendees(List<UUID> attendees,
//                                                                      Duration duration, ZonedDateTime start,
//                                                                      ZonedDateTime end) {
//
//        List<Range<ZonedDateTime>> availablePeriods = new ArrayList<>();
//
//
//        // get all working schedule
//        availablePeriods.add(Period.ofDays(1));
//    }
}
