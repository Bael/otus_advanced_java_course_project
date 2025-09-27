package com.github.bael.otus.java.calendar.domain;

import lombok.Builder;

import java.time.DayOfWeek;
import java.util.*;


@Builder
public class WeeklySchedule {
//    private Map<DayOfWeek, List<Period>> weeklyOpenPeriods;
//    private Map<DayOfWeek, List<Period>> closedPeriods() {
//        Map<DayOfWeek, List<Period>> result = new HashMap<>();
//
//    };
//
//
//    public List<Period> getOpenPeriods(DayOfWeek dayOfWeek) {
//        return weeklyOpenPeriods.getOrDefault(dayOfWeek, Collections.emptyList());
//    }

//    public List<Period> getClosedPeriods(DayOfWeek dayOfWeek) {
//        List<Period> opened = weeklyOpenPeriods.getOrDefault(dayOfWeek, Collections.emptyList());
//        boolean[] openedPeriods = new boolean[24];
//
//        for (Period period : opened) {
//            for (int i = period.getStartHour(); i <= period.getFinishHour(); i++) {
//                openedPeriods[i] = true;
//            }
//        }
//
//        List<Period> result = new ArrayList<>();
//        for (int i = 0; i < 24; i++) {
//            if (!openedPeriods[i]) {
//
//            }
//
//        }
//
//
//
//
//
//    }

//    public List<Period> getMondayOpenPeriods() {
//        return getMondayOpenPeriods(DayOfWeek.MONDAY);
//    }





}
