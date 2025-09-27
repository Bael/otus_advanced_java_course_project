package com.github.bael.otus.java.calendar.domain;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;

public class Scheduler {
    public static void main(String[] args) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(com.cronutils.model.CronType.QUARTZ));
        Cron cron = parser.parse("0 0 19 ? 1-5,9-12 MON,WED");
        ExecutionTime executionTime = ExecutionTime.forCron(cron);



        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime end = now.plusYears(2);

        ZonedDateTime current = now;
        while (!current.isAfter(end)) {
            var next = executionTime.nextExecution(current);
            if (next.isEmpty() || next.get().isAfter(end)) break;
            System.out.println(next.get());
            current = next.get().plusSeconds(1); // избегаем зацикливания
        }
    }
}
