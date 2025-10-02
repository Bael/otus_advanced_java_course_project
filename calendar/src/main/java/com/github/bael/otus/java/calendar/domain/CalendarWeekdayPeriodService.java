package com.github.bael.otus.java.calendar.domain;

import com.github.bael.otus.java.calendar.data.CalendarRepository;
import com.github.bael.otus.java.calendar.data.CalendarWeekdayPeriodRepository;
import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.entity.CalendarWeekdayPeriod;
import com.github.bael.otus.java.calendar.rest.dto.WeekdayPeriodRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarWeekdayPeriodService {

    private final CalendarWeekdayPeriodRepository calendarWeekdayPeriodRepository;
    private final CalendarRepository calendarRepository;

    @Transactional
    public List<CalendarWeekdayPeriod> getWeeklySchedules(UUID calendarId, UUID userId) {

        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow();

        return calendarWeekdayPeriodRepository.findByCalendarAndUserId(calendar, userId);
    }

    @Transactional
    public void setWeeklySchedule(UUID calendarId, UUID userId, WeekdayPeriodRequest request) {
        // 1. Удаляем старые периоды
        calendarWeekdayPeriodRepository.deleteAllByCalendar_IdAndUserId(calendarId, userId);
        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow();

        // 2. Сохраняем новые
        List<CalendarWeekdayPeriod> periods = request.periods().stream()
                .map(period -> {
                    CalendarWeekdayPeriod entity = new CalendarWeekdayPeriod();
                    entity.setUserId(userId);
                    entity.setWeekDay(period.dayOfWeek());
                    entity.setCalendar(calendar);
                    entity.setStart(period.start());
                    entity.setFinish(period.finish());
                    return entity;
                })
                .collect(Collectors.toList());

        calendarWeekdayPeriodRepository.saveAll(periods);
    }


    public Map<DayOfWeek, List<Range<LocalTime>>> getIntersectionPeriods(Calendar calendar, UUID organizer,
                                                                         List<UUID> attendees) {


        int edge = attendees.size() + 1;
        List<UUID> attendeesList = new ArrayList<>(attendees);
        attendeesList.add(organizer);

        Map<DayOfWeek, List<List<Range<LocalTime>>>> weeklyOpenPeriods = new HashMap<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            weeklyOpenPeriods.put(dayOfWeek, new ArrayList<>());
        }
        for (UUID uuid : attendeesList) {
            List<CalendarWeekdayPeriod> userList = calendarWeekdayPeriodRepository.findByCalendarAndUserId(calendar, uuid);
            var map = userList.stream().collect(Collectors.groupingBy(CalendarWeekdayPeriod::getWeekDay,
                    Collectors.toList()));

            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                List<List<Range<LocalTime>>> lists = weeklyOpenPeriods.get(dayOfWeek);
                var list = map.getOrDefault(dayOfWeek, Collections.emptyList());
                lists.add(list.stream().map(period -> Range.closed(period.getStart(), period.getFinish())).collect(Collectors.toList()));
            }
        }


        Map<DayOfWeek, List<Range<LocalTime>>> dayOfWeekListMap = WeeklyHourlySchedule.calculateIntersections(weeklyOpenPeriods, edge);
        return dayOfWeekListMap;
    }
}
