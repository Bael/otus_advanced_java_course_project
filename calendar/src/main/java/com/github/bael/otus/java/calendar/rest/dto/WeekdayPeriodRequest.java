package com.github.bael.otus.java.calendar.rest.dto;

// com.github.bael.otus.java.calendar.dto.WeekdayPeriodRequest

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record WeekdayPeriodRequest(
        List<Period> periods
) {
    public record Period(
            DayOfWeek dayOfWeek,
            LocalTime start,
            LocalTime finish
    ) {
        @JsonCreator
        public Period(
                @JsonProperty("dayOfWeek")


                DayOfWeek dayOfWeek,
                @JsonProperty("start") @JsonFormat(pattern = "HH:mm:ss") LocalTime start,
                @JsonProperty("finish") @JsonFormat(pattern = "HH:mm:ss") LocalTime finish
        ) {
            this.dayOfWeek = dayOfWeek;
            this.start = start;
            this.finish = finish;
        }
    }
}