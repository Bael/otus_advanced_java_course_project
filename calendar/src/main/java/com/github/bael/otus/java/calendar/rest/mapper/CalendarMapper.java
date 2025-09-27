package com.github.bael.otus.java.calendar.rest.mapper;

import com.github.bael.otus.java.calendar.entity.Calendar;
import com.github.bael.otus.java.calendar.rest.dto.CalendarCreateRequest;
import com.github.bael.otus.java.calendar.rest.dto.CalendarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CalendarMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Calendar toEntity(CalendarCreateRequest request);

    CalendarResponse toResponse(Calendar calendar);
}