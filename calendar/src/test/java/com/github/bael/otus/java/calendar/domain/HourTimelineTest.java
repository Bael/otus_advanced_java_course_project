package com.github.bael.otus.java.calendar.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HourTimelineTest {

    @Test
    void name() {
        LocalDate start = LocalDate.of(2025, 9, 22);
        HourTimeline  hourTimeline = new HourTimeline(start,
                LocalDate.of(2025, 9, 28));

        var period1Start = start.atStartOfDay().plusDays(3).plusHours(10);
        var period1Finish = period1Start.plusHours(3);

        var period2Start = start.atStartOfDay().plusDays(4).plusHours(11);
        var period2Finish = period2Start.plusHours(2);


        hourTimeline.addOpenPeriod(period1Start, period1Finish);
        hourTimeline.addOpenPeriod(period2Start, period2Finish);

        System.out.println(hourTimeline.getOpenPeriods());
    }
}