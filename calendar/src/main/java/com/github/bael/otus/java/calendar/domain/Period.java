package com.github.bael.otus.java.calendar.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Period {
    private int startHour;
    private int finishHour;

    public static Period of(int i, int i1) {
        return Period.builder().startHour(i).finishHour(i1).build();
    }

    public void shiftFinish() {
        finishHour++;
    }

    @Override
    public String toString() {
        return "Period{" +
                startHour + ":" + finishHour + "}";
    }

    public int getDuration() {
        return finishHour - startHour + 1;
    }

}
