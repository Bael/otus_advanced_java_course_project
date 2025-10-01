package com.github.bael.otus.java.calendar.domain;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Range;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyHourlyScheduleTest {
    @Test
    void name() {
        LocalTime time1Start = LocalTime.of(8, 0);
        LocalTime time1Finish = LocalTime.of(9, 0);

        LocalTime time2Start = LocalTime.of(7, 0);
        LocalTime time2Finish = LocalTime.of(8, 30);


        LocalTime time3Start = LocalTime.of(8, 0);
        LocalTime time3Finish = LocalTime.of(9, 1);

        LocalTime time4Start = LocalTime.of(9, 0);
        LocalTime time4Finish = LocalTime.of(9, 30);

        WeeklyHourlySchedule weeklyHourlySchedule = new WeeklyHourlySchedule();
        Map<DayOfWeek, List<List<Range<LocalTime>>>> schedules = Map.of(
                DayOfWeek.MONDAY, List.of(List.of(Range.closed(time1Start, time1Finish)), List.of(Range.closed(time2Start, time2Finish))),
                DayOfWeek.TUESDAY, List.of(List.of(Range.closed(time3Start, time3Finish)), List.of(Range.closed(time4Start, time4Finish)))
        );


        Map<DayOfWeek, List<Range<LocalTime>>> dayOfWeekListMap = weeklyHourlySchedule.calculateIntersections(schedules, 2);
        System.out.println(dayOfWeekListMap);

    }
}