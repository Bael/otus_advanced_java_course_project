package com.github.bael.otus.java.calendar.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface Timeline {
    void addOpenPeriod(LocalDateTime start, LocalDateTime finish);
    void closePeriod(LocalDateTime start, LocalDateTime finish);

    Map<LocalDate, List<Period>> getOpenPeriods();

//    /**
//     * возвращает новый timeline пересеченный по открытым периодам с текущим
//     * @param timeline
//     * @return
//     */
//    Timeline intersect(Timeline timeline);


}

