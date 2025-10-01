package com.github.bael.otus.java.calendar.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HourTimeline implements Timeline {
    private final LocalDate startDate;
    private final LocalDate finishDate;
    private final boolean[] hours;
    private static final int HOURS_PER_DAY = 24;

    public HourTimeline(LocalDate startDate, LocalDate finishDate) {
        if (startDate == null || finishDate == null) {
            throw new IllegalArgumentException("Start and finish dates cannot be null");
        }
        if (startDate.isAfter(finishDate)) {
            throw new IllegalArgumentException("Start date cannot be after finish date");
        }

        this.startDate = startDate;
        this.finishDate = finishDate;

        long days = Duration.between(startDate.atStartOfDay(), finishDate.plusDays(1).atStartOfDay()).toDays();
        this.hours = new boolean[(int) days * HOURS_PER_DAY];
    }

    @Override
    public void addOpenPeriod(LocalDateTime start, LocalDateTime finish) {
        addPeriod(start, finish, true);
    }

    private void addPeriod(LocalDateTime start, LocalDateTime finish, boolean isOpened) {
        validateDateTime(start, finish);

        LocalDateTime periodStart = start.isAfter(startDate.atStartOfDay()) ? start : startDate.atStartOfDay();
        LocalDateTime periodFinish = finish.isBefore(finishDate.plusDays(1).atStartOfDay()) ?
                finish : finishDate.plusDays(1).atStartOfDay();

        if (!periodStart.isBefore(periodFinish)) {
            return; // Нет пересечения с периодом timeline
        }

        LocalDateTime current = periodStart;
        while (current.isBefore(periodFinish)) {
            int hourIndex = getHourIndex(current);
            if (hourIndex >= 0 && hourIndex < hours.length) {
                hours[hourIndex] = isOpened;
            }
            current = current.plusHours(1);
        }
    }

    private void validateDateTime(LocalDateTime start, LocalDateTime finish) {
        if (start == null || finish == null) {
            throw new IllegalArgumentException("Start and finish cannot be null");
        }
        if (start.isAfter(finish)) {
            throw new IllegalArgumentException("Start cannot be after finish");
        }
        if (start.toLocalDate().isAfter(finishDate) || finish.toLocalDate().isBefore(startDate)) {
            throw new IllegalArgumentException("Period is outside timeline range");
        }
    }

    public int getHourIndex(LocalDateTime time) {
        if (time.toLocalDate().isBefore(startDate) || time.toLocalDate().isAfter(finishDate)) {
            return -1; // Вне диапазона
        }
        long days = Duration.between(startDate.atStartOfDay(), time.toLocalDate().atStartOfDay()).toDays();
        return (int) days * HOURS_PER_DAY + time.getHour();
    }

    @Override
    public void closePeriod(LocalDateTime start, LocalDateTime finish) {
        addPeriod(start, finish, false);
    }

    @Override
    public Map<LocalDate, List<Period>> getOpenPeriods() {
        Map<LocalDate, List<Period>> openPeriods = new HashMap<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(finishDate)) {
            openPeriods.put(currentDate, getOpenPeriodsOnDate(currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return openPeriods;
    }



    private List<Period> getOpenPeriodsOnDate(LocalDate date) {
        List<Period> openPeriods = new ArrayList<>();

        int startHourIndex = getHourIndex(date.atStartOfDay());
        if (startHourIndex < 0) return openPeriods;

        Period currentPeriod = null;

        for (int hour = 0; hour < HOURS_PER_DAY; hour++) {
            int hourIndex = startHourIndex + hour;
            if (hourIndex >= hours.length) break;

            if (hours[hourIndex]) {
                if (currentPeriod == null) {
                    currentPeriod = Period.of(hour, hour);
                } else {
                    currentPeriod = Period.of(currentPeriod.getStartHour(), hour);
                }
            } else {
                if (currentPeriod != null) {
                    openPeriods.add(currentPeriod);
                    currentPeriod = null;
                }
            }
        }

        // Добавляем последний период, если он есть
        if (currentPeriod != null) {
            openPeriods.add(currentPeriod);
        }

        return openPeriods;
    }

    // Вспомогательный метод для тестирования
    public boolean isHourOpen(LocalDateTime dateTime) {
        int hourIndex = getHourIndex(dateTime);
        return hourIndex >= 0 && hourIndex < hours.length && hours[hourIndex];
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }
}