package com.github.bael.otus.java.calendar.domain;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerJob {

    private int horizonDays = 30;

    @Scheduled(fixedRate = 3600_000)
    public void schedule() {
        // find all recurrent rules
        // plan on next N days

    }
}
