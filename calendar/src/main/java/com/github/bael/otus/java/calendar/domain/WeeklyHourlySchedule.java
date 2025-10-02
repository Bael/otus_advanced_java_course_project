package com.github.bael.otus.java.calendar.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Range;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;


/**
 * вычисляем общее время
 */
public class WeeklyHourlySchedule {


    public static Map<DayOfWeek, List<Range<LocalTime>>>  calculateIntersections(Map<DayOfWeek, List<List<Range<LocalTime>>>> weeklyOpenPeriods, int edge) {
        Map<DayOfWeek, List<Range<LocalTime>>> intersectionsWeekMap = new HashMap<>();
        Map<DayOfWeek, TreeMap<LocalTime, Integer>> startWeekMap = new HashMap<>();
        Map<DayOfWeek, TreeMap<LocalTime, Integer>> finishWeekMap = new HashMap<>();

        // add points
        for (var dayOfWeek : DayOfWeek.values()) {
            // init
            TreeMap<LocalTime, Integer> dayStartMap = new TreeMap<>();
            TreeMap<LocalTime, Integer> dayFinishMap = new TreeMap<>();
            startWeekMap.put(dayOfWeek, dayStartMap);
            finishWeekMap.put(dayOfWeek, dayFinishMap);

            List<Range<LocalTime>> intersections = new ArrayList<>();
            intersectionsWeekMap.put(dayOfWeek, intersections);
            // add points
            if (!weeklyOpenPeriods.containsKey(dayOfWeek)) {
                continue;
            }
            for (var listOfList : weeklyOpenPeriods.get(dayOfWeek)) {
                listOfList.forEach(dayOfWeekPeriod -> {
                    var startPoint = dayOfWeekPeriod.getLowerBound().getValue().orElseThrow();
                    var endPoint = dayOfWeekPeriod.getUpperBound().getValue().orElseThrow();

                    var defaultStartValue = dayStartMap.getOrDefault(startPoint, 0);
                    var defaultFinishValue = dayFinishMap.getOrDefault(endPoint, 0);
                    dayStartMap.put(startPoint, defaultStartValue + 1);
                    dayFinishMap.put(endPoint, defaultFinishValue + 1);
                });
            }

            // extract
            int startCounter = 0;
            int finishCounter = 0;
            for (var dayStartEntry : dayStartMap.entrySet()) {
                Integer startedPeriodsCountOnTime = dayStartEntry.getValue();
                startCounter += startedPeriodsCountOnTime;

                var finishMap = dayFinishMap.headMap(dayStartEntry.getKey(), true);
                for (var dayFinishEntry : finishMap.entrySet()) {
                    Integer finishedPeriodsCountOnTime = dayFinishEntry.getValue();
                    finishCounter += finishedPeriodsCountOnTime;
                }
                int diff =  startCounter - finishCounter;
                if (diff >= edge) {
                    var nextFinish = dayFinishMap.ceilingEntry(dayStartEntry.getKey());
                    intersections.add(Range.closed(dayStartEntry.getKey(), nextFinish.getKey()));
                }
            }

        }
        return intersectionsWeekMap;
    }

}
