package com.github.bael.otus.java.calendar.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class HourTimeline implements Timeline {
    private final LocalDate startDate;
    private final LocalDate finishDate;
    boolean[] hours;


    public HourTimeline(LocalDate startDate, LocalDate finishDate) {
        this.startDate = startDate;
        this.finishDate = finishDate;

        hours = new boolean[24 *  (int) (1 + finishDate.toEpochDay() - startDate.toEpochDay())];
    }

    @Override
    public void addOpenPeriod(LocalDateTime start, LocalDateTime finish) {
        addPeriod(start, finish, true);
    }

    public void addPeriod(LocalDateTime start, LocalDateTime finish, boolean isOpened) {
        LocalDateTime periodStart = startDate.atStartOfDay();
        if (periodStart.isBefore(start)) {
            periodStart = start;
        }
        LocalDateTime periodFinish = finishDate.plusDays(1).atStartOfDay();
        if (periodFinish.isAfter(finish)) {
            periodFinish = finish;
        }

        LocalDateTime next = periodStart;
        do {
            next = next.plusHours(1);
            int hour = this.getHour(next);
            hours[hour] = isOpened;
        } while (next.isBefore(periodFinish));
    }

    private static int HOURS_PER_DAY = 24;
    public int getHour(LocalDateTime time) {
        int day = (int) (time.toLocalDate().toEpochDay() - this.startDate.toEpochDay());
        return day * HOURS_PER_DAY + time.getHour();

    }

    @Override
    public void closePeriod(LocalDateTime start, LocalDateTime finish) {
        addPeriod(start, finish, false);
    }

    @Override
    public List<Period> getOpenPeriods() {
        List<Period> openPeriods = new ArrayList<>();
        LocalDate next = this.startDate;
        LocalDate finish = this.finishDate;
        do {
            var opened = getOpenPeriodsOnDate(next);
            System.out.println("on date: " + next + ", opened: " + opened);
            openPeriods.addAll(opened);
            next = next.plusDays(1);
        }
        while (next.isBefore(finish));



        return List.of();
    }

    private List<Period> getOpenPeriodsOnDate(LocalDate next) {
        List<Period> openPeriods = new ArrayList<>();
        LocalDateTime localDateTime = next.atStartOfDay();
        int hour = this.getHour(localDateTime);
        Period current = null;
        for (int i = 0; i < HOURS_PER_DAY; i++) {
            if (hours[hour + i]) {
                if (current == null) {
                    current = Period.of(i, i);
                } else {
                    current.shiftFinish();
                }
            } else {
                // есть что записать
                if (current != null) {
                    openPeriods.add(current);
                    current = null;
                }
            }
        }
        // Добавляем последний период, если он есть
        if (current != null) {
            openPeriods.add(current);
        }

        return openPeriods;
    }
}
