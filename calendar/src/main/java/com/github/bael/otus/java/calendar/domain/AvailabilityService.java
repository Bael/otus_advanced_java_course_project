package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarWeekdayPeriodRepository;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.entity.CalendarEventStatus;
import com.github.bael.otus.java.calendar.entity.CalendarWeekdayPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AvailabilityService {


    private final CalendarEventService  calendarEventService;
    private final CalendarWeekdayPeriodService calendarWeekdayPeriodService;

    public List<Range<LocalDateTime>> getAvailablePeriodsUTC(Calendar calendar, UUID organizer,
                                                               LocalDate startDate, LocalDate endDate,
                                                            List<UUID> attendees, Duration duration) {

        List<Range<LocalDateTime>> availablePeriodsList = new ArrayList<>();

        List<UUID> allAttendees = new ArrayList<>(attendees);
        allAttendees.add(organizer);

        Map<DayOfWeek, List<Range<LocalTime>>> intersectionPeriods = calendarWeekdayPeriodService.getIntersectionPeriods(calendar, organizer, attendees);
        LocalDate current = startDate;

        ZoneId utcZone = ZoneOffset.UTC;

        do {
            LocalDateTime currentDateTimeStart = LocalDateTime.of(current, LocalTime.MIDNIGHT);
            LocalDateTime currentDateTimeEnd = currentDateTimeStart.plusDays(1).minusMinutes(1);

            LocalDate currentFinal = current;

            List<Range<LocalTime>> dayPeriods = intersectionPeriods.get(current.getDayOfWeek());
            if (dayPeriods == null || dayPeriods.isEmpty()) {
                current = current.plusDays(1);
                continue;
            }

            List<Range<LocalDateTime>> availablePeriods = dayPeriods.stream()
                    .map(range -> mapRange(currentFinal, range)).toList();


            // получаем доступные периоды
            TreeMapTimeline availableTimeline = new TreeMapTimeline(availablePeriods);

            // сливаем занятые событиями для любого из участников куски

            TreeMapTimeline eventsTimeline = new TreeMapTimeline();
            calendarEventService.findByCalendarAndPeriodAndAttendeeList(calendar.getId(), currentDateTimeStart,
                            currentDateTimeEnd, allAttendees)
                    .stream()
                    .filter(calendarEvent -> calendarEvent.getStatus() == CalendarEventStatus.CONFIRMED)
                    .forEach(event -> 
                                eventsTimeline.addPeriod( 
                                    LocalDateTime.ofInstant(event.getStartTime(), utcZone),
                                    LocalDateTime.ofInstant(event.getEndTime(), utcZone)));

            var freeRanges = subtract(availableTimeline.getRanges(), eventsTimeline.getRanges());

            for (Range<LocalDateTime> range : freeRanges) {
                LocalDateTime start = range.getLowerBound().getValue().get();
                LocalDateTime end = range.getUpperBound().getValue().get();
                if (Duration.between(start, end).compareTo(duration) >= 0) {
                    availablePeriodsList.add(Range.closed(start, start.plus(duration)));
                }
            }
            current = current.plusDays(1);
        } while (!current.isAfter(endDate));
        return availablePeriodsList;

    }

    public static List<Range<LocalDateTime>> subtract(
            List<Range<LocalDateTime>> free,
            List<Range<LocalDateTime>> busy) {

        if (busy.isEmpty()) return new ArrayList<>(free);
        if (free.isEmpty()) return List.of();

        List<Range<LocalDateTime>> result = new ArrayList<>();
        List<Range<LocalDateTime>> sortedBusy = busy.stream()
                .sorted(Comparator.comparing(r -> r.getLowerBound().getValue().get()))
                .toList();

        for (Range<LocalDateTime> freeRange : free) {
            LocalDateTime currentStart = freeRange.getLowerBound().getValue().get();
            LocalDateTime freeEnd = freeRange.getUpperBound().getValue().get();

            for (Range<LocalDateTime> busyRange : sortedBusy) {
                LocalDateTime busyStart = busyRange.getLowerBound().getValue().get();
                LocalDateTime busyEnd = busyRange.getUpperBound().getValue().get();

                if (busyEnd.isBefore(currentStart) || busyStart.isAfter(freeEnd)) {
                    continue; // no overlap
                }

                if (busyStart.isAfter(currentStart)) {
                    // есть свободный кусок ДО занятого
                    result.add(Range.closed(currentStart, busyStart));
                }

                // сдвигаем начало за конец занятого
                currentStart = busyEnd.isAfter(currentStart) ? busyEnd : currentStart;
                if (currentStart.isAfter(freeEnd)) break;
            }

            // остаток после всех busy
            if (currentStart.isBefore(freeEnd)) {
                result.add(Range.closed(currentStart, freeEnd));
            }
        }

        return result;
    }

    private Range<LocalDateTime> mapRange(LocalDate currentFinal, Range<LocalTime> range) {
        return  Range.closed(currentFinal.atTime(range.getLowerBound().getValue().get()),
                        currentFinal.atTime(range.getUpperBound().getValue().get()));

    }

}
