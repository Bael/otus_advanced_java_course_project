package com.github.bael.otus.java.calendar.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TreeMapTimelineTest {
    @Test
    void name() {
        TreeMapTimeline treeMapTimeline = new TreeMapTimeline();
        treeMapTimeline.addPeriod(LocalDateTime.of(2020, 1, 1, 1, 1),
                LocalDateTime.of(2020, 1, 1, 2, 2));

        treeMapTimeline.addPeriod(LocalDateTime.of(2020, 1, 1, 1, 30),
                LocalDateTime.of(2020, 1, 1, 2, 10));

        System.out.println(treeMapTimeline.getRanges());

        var merged = treeMapTimeline.getRanges();
        TreeMapTimeline mergedTimeline = new TreeMapTimeline(merged);

        mergedTimeline.subPeriod(LocalDateTime.of(2020, 1, 1, 1, 30),
                LocalDateTime.of(2020, 1, 1, 1, 40));
        System.out.println(mergedTimeline.getRanges());
    }
}