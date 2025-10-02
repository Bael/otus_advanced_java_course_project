package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.entity.CalendarEventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Range;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    private AvailabilityService availabilityService;

    private CalendarEventService calendarEventService;
    private CalendarWeekdayPeriodService calendarWeekdayPeriodService;

    private final UUID calendarId = UUID.randomUUID();
    private final UUID organizerId = UUID.randomUUID();
    private final UUID attendeeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        calendarEventService = mock(CalendarEventService.class);
        calendarWeekdayPeriodService = mock(CalendarWeekdayPeriodService.class);
        availabilityService = new AvailabilityService(calendarEventService, calendarWeekdayPeriodService);
    }

    @Test
    void shouldReturnAvailableSlotsAfterSubtractingBusyPeriods() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 3); // понедельник
        Duration duration = Duration.ofHours(1);

        Calendar calendar = new Calendar();
        calendar.setId(calendarId);
        // Предполагаем, что календарь в UTC — по вашему уточнению

        // 1. Настройка расписания: пн–пт 9:00–17:00 (UTC)
        Map<DayOfWeek, List<Range<LocalTime>>> intersectionPeriods = new HashMap<>();
        intersectionPeriods.put(DayOfWeek.MONDAY,
                List.of(Range.closed(LocalTime.of(9, 0), LocalTime.of(17, 0))));
        when(calendarWeekdayPeriodService.getIntersectionPeriods(calendar, organizerId, List.of(attendeeId)))
                .thenReturn(intersectionPeriods);

        // 2. Занятое событие: 10:00–11:00 UTC
        CalendarEvent busyEvent = new CalendarEvent();
        busyEvent.setStartTime(testDate.atTime(10, 0).toInstant(ZoneOffset.UTC));
        busyEvent.setEndTime(testDate.atTime(11, 0).toInstant(ZoneOffset.UTC));
        busyEvent.setStatus(CalendarEventStatus.CONFIRMED);

        LocalDateTime dayStart = testDate.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1); // [00:00, 24:00)

        when(calendarEventService.findByCalendarAndPeriodAndAttendeeList(
                calendarId, dayStart, dayEnd, List.of(attendeeId, organizerId)))
                .thenReturn(List.of(busyEvent));

        // When
        List<Range<LocalDateTime>> result = availabilityService.getAvailablePeriodsUTC(
                calendar,
                organizerId,
                testDate,
                testDate,
                List.of(attendeeId),
                duration
        );

        System.out.println(">>>>");
        System.out.println(result);
        System.out.println(">>>>");
        // Then
        assertThat(result).hasSize(2);

        // Ожидаем: [09:00–10:00] и [11:00–12:00] (первые два слота по 1 часу)
        Range<LocalDateTime> slot1 = result.get(0);
        Range<LocalDateTime> slot2 = result.get(1);

        assertThat(slot1.getLowerBound().getValue().get())
                .isEqualTo(testDate.atTime(9, 0));
        assertThat(slot1.getUpperBound().getValue().get())
                .isEqualTo(testDate.atTime(10, 0));

        assertThat(slot2.getLowerBound().getValue().get())
                .isEqualTo(testDate.atTime(11, 0));
        assertThat(slot2.getUpperBound().getValue().get())
                .isEqualTo(testDate.atTime(12, 0));
    }

    @Test
    void shouldReturnEmptyWhenNoFreeTimeOnDay() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 3); // понедельник

        Calendar calendar = new Calendar();
        calendar.setId(calendarId);

        // Нет свободного времени в понедельник
        Map<DayOfWeek, List<Range<LocalTime>>> intersectionPeriods = new HashMap<>();
        // MONDAY отсутствует
        when(calendarWeekdayPeriodService.getIntersectionPeriods(calendar, organizerId, List.of(attendeeId)))
                .thenReturn(intersectionPeriods);

        // When
        List<Range<LocalDateTime>> result = availabilityService.getAvailablePeriodsUTC(
                calendar,
                organizerId,
                testDate,
                testDate,
                List.of(attendeeId),
                Duration.ofHours(1)
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotIncludeCancelledEvents() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 3);

        Calendar calendar = new Calendar();
        calendar.setId(calendarId);

        Map<DayOfWeek, List<Range<LocalTime>>> intersectionPeriods = new HashMap<>();
        intersectionPeriods.put(DayOfWeek.MONDAY,
                List.of(Range.closed(LocalTime.of(9, 0), LocalTime.of(17, 0))));
        when(calendarWeekdayPeriodService.getIntersectionPeriods(calendar, organizerId, List.of(attendeeId)))
                .thenReturn(intersectionPeriods);

        // Отменённое событие — не должно учитываться
        CalendarEvent cancelledEvent = new CalendarEvent();
        cancelledEvent.setStartTime(testDate.atTime(10, 0).toInstant(ZoneOffset.UTC));
        cancelledEvent.setEndTime(testDate.atTime(11, 0).toInstant(ZoneOffset.UTC));
        cancelledEvent.setStatus(CalendarEventStatus.CANCELLED);

        LocalDateTime dayStart = testDate.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        when(calendarEventService.findByCalendarAndPeriodAndAttendeeList(
                calendarId, dayStart, dayEnd, List.of(attendeeId, organizerId)))
                .thenReturn(List.of(cancelledEvent));

        // When
        List<Range<LocalDateTime>> result = availabilityService.getAvailablePeriodsUTC(
                calendar,
                organizerId,
                testDate,
                testDate,
                List.of(attendeeId),
                Duration.ofHours(1)
        );

        // Then: должно быть много слотов, но возьмём первый
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getLowerBound().getValue().get())
                .isEqualTo(testDate.atTime(9, 0));
    }
}