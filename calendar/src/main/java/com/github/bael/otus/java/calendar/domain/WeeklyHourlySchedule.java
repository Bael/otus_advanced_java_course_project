package com.github.bael.otus.java.calendar.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Range;
import org.springframework.data.relational.core.sql.In;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;


//@Builder
//@NoArgsConstructor
public class WeeklyHourlySchedule {


    @Getter
    Map<DayOfWeek, List<Range<LocalTime>>> intersections = new HashMap<>();

    public Map<DayOfWeek, List<Range<LocalTime>>>  calculateIntersections(Map<DayOfWeek, List<List<Range<LocalTime>>>> weeklyOpenPeriods, int edge) {
        Map<DayOfWeek, List<Range<LocalTime>>> intersectionsWeekMap = new HashMap<>();
        Map<DayOfWeek, TreeMap<LocalTime, Integer>> startWeekMap = new HashMap<>();
        Map<DayOfWeek, TreeMap<LocalTime, Integer>> finishWeekMap = new HashMap<>();
//        int edge = weeklyOpenPeriods.size();

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
//            System.out.println("edge  " + edge);
            for (var dayStartEntry : dayStartMap.entrySet()) {
                Integer startedPeriodsCountOnTime = dayStartEntry.getValue();
                startCounter += startedPeriodsCountOnTime;
//                System.out.println("start  " + startCounter);

                var finishMap = dayFinishMap.headMap(dayStartEntry.getKey(), true);
                for (var dayFinishEntry : finishMap.entrySet()) {
                    Integer finishedPeriodsCountOnTime = dayFinishEntry.getValue();
                    finishCounter += finishedPeriodsCountOnTime;
                }
//                System.out.println("finish " + finishCounter);
                int diff =  startCounter - finishCounter;
//                System.out.println("diff " + diff);
                if (diff >= edge) {

                    // add start range
//                    System.out.println("dayStartEntry.getKey() " + dayStartEntry.getKey());
//                    System.out.println("dayFinishMap " + dayFinishMap);
                    var nextFinish = dayFinishMap.ceilingEntry(dayStartEntry.getKey());

                    intersections.add(Range.closed(dayStartEntry.getKey(), nextFinish.getKey()));
//                    System.out.println("added intersection");
                }
            }

        }
        return intersectionsWeekMap;
    }

}
