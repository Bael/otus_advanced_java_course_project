package com.github.bael.otus.java.calendar.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
public interface Timeline {
    void addOpenPeriod(LocalDateTime start, LocalDateTime finish);
    void closePeriod(LocalDateTime start, LocalDateTime finish);

    List<Period> getOpenPeriods();

}

