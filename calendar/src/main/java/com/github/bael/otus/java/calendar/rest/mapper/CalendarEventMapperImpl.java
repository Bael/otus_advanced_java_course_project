package com.github.bael.otus.java.calendar.rest.mapper;

import com.github.bael.otus.java.calendar.data.CalendarEventRepository;
import com.github.bael.otus.java.calendar.domain.CalendarService;
import com.github.bael.otus.java.calendar.entity.CalendarEvent;
import com.github.bael.otus.java.calendar.entity.CalendarEventStatus;
import com.github.bael.otus.java.calendar.entity.EventType;
import com.github.bael.otus.java.calendar.rest.dto.CalendarEventCreateRequest;
import com.github.bael.otus.java.calendar.rest.dto.CalendarEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarEventMapperImpl implements CalendarEventMapper {

    private final CalendarService calendarService;

    @Override
    public CalendarEvent toEntity(CalendarEventCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        CalendarEvent calendarEvent = new CalendarEvent();

        calendarEvent.setTitle( request.getTitle() );
        calendarEvent.setCalendar(calendarService.getCalendarById(request.getCalendarId()).orElseThrow());
        calendarEvent.setUserId( request.getUserId() );
        calendarEvent.setDescription( request.getDescription() );
        calendarEvent.setStartTime( ZoneTimeMapper.toInstant(request.getStartTime(), request.getTimezone()));
        calendarEvent.setEndTime( ZoneTimeMapper.toInstant(request.getEndTime(), request.getTimezone()));

        if (request.getEventType() != null ) {
            calendarEvent.setEventType( Enum.valueOf( EventType.class, request.getEventType() ) );
        }
        calendarEvent.setLocation( request.getLocation() );
        if ( request.getStatus() != null ) {
            calendarEvent.setStatus( Enum.valueOf( CalendarEventStatus.class, request.getStatus() ) );
        }
        calendarEvent.setScheduleCronExpression( request.getScheduleCronExpression() );

        return calendarEvent;
    }

    @Override
    public CalendarEventResponse toResponse(CalendarEvent event, String timeZone) {
        if ( event == null ) {
            return null;
        }

        CalendarEventResponse calendarEventResponse = new CalendarEventResponse();

        calendarEventResponse.setId( event.getId() );
        calendarEventResponse.setTitle( event.getTitle() );
        calendarEventResponse.setCalendarId( event.getCalendar().getId() );
        calendarEventResponse.setDescription( event.getDescription() );
        calendarEventResponse.setStartTime( ZoneTimeMapper.toLocalDateTime(event.getStartTime(), timeZone));
        calendarEventResponse.setEndTime( ZoneTimeMapper.toLocalDateTime(event.getEndTime(), timeZone) );
        if ( event.getEventType() != null ) {
            calendarEventResponse.setEventType( event.getEventType().name() );
        }
        calendarEventResponse.setLocation( event.getLocation() );
        calendarEventResponse.setScheduleCronExpression( event.getScheduleCronExpression() );
        if ( event.getStatus() != null ) {
            calendarEventResponse.setStatus( event.getStatus().name() );
        }

        return calendarEventResponse;
    }
}
