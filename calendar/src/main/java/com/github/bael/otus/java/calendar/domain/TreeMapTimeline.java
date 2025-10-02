package com.github.bael.otus.java.calendar.domain;

import lombok.Builder;
import org.springframework.data.domain.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class TreeMapTimeline  {
    private TreeMap<LocalDateTime, Integer> timeline = new TreeMap<>();

    public TreeMapTimeline() {
    }

    public TreeMapTimeline(List<Range<LocalDateTime>> ranges) {
        ranges.forEach(localDateRange -> addPeriod(localDateRange.getLowerBound().getValue().get(),
                localDateRange.getUpperBound().getValue().get()));
    }

    /**
     * сливаем указанный отрезок с текущими
     *
     * @param start
     * @param finish
     */
    public void addPeriod(LocalDateTime start, LocalDateTime finish) {
        setPeriod(start, finish, 1);
    }

    public void subPeriod(LocalDateTime start, LocalDateTime finish) {
        setPeriod(start, finish, -1);
    }

    private void setPeriod(LocalDateTime start, LocalDateTime finish, int multiplier) {
        int currentStart = timeline.getOrDefault(start, 0);
        timeline.put(start, currentStart + (multiplier));

        int currentFinish = timeline.getOrDefault(finish, 0);
        timeline.put(finish, currentFinish - (multiplier));
    }




    public List<Range<LocalDateTime>> getRanges() {
        int current = 0;
        List<Range<LocalDateTime>> result = new ArrayList<>();
        LocalDateTime start = null;
        LocalDateTime finish = null;

        for (var entry : timeline.entrySet()) {
            current += entry.getValue();
            System.out.println("date" + entry.getKey()  +  " current: " + current + ", start: " + start + "finish "  + finish);
            if (current > 0) {
                if (start == null) {
                    start = entry.getKey();
                }
                //  else continue
            } else {
                finish = entry.getKey();
                result.add(Range.closed(start, finish));
                start = null;
                finish = null;
            }
        }

        return result;
    }
}
