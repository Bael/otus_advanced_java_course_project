package com.github.bael.otus.java.calendar.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerJob {

    private final int horizonDays = 30;


    @Scheduled(fixedRate = 60_000)
    public void createRecurringEvents() {

        // find all recurrent rules
        // plan on next N days

    }
}
