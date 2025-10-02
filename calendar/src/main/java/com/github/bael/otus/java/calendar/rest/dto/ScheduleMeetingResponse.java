package com.github.bael.otus.java.calendar.rest.dto;

import lombok.Data;
import org.springframework.data.domain.Range;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleMeetingResponse {
    private String result;
    private List<Range<LocalDateTime>> slots;

}
