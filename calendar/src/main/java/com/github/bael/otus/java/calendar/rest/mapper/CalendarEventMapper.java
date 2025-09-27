package com.github.bael.otus.java.calendar.rest.mapper;

import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.rest.dto.CalendarEventCreateRequest;
import com.github.bael.otus.java.calendar.rest.dto.CalendarEventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parentEvent", ignore = true)
    CalendarEvent toEntity(CalendarEventCreateRequest request);

    CalendarEventResponse toResponse(CalendarEvent event);
}