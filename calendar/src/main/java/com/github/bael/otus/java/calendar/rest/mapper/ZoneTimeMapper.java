package com.github.bael.otus.java.calendar.rest.mapper;

import java.time.*;

public class ZoneTimeMapper {

    /**
     * Преобразует локальное время клиента (в его часовом поясе) в момент времени в UTC.
     *
     * @param clientLocalDateTime локальное время клиента (без зоны)
     * @param clientTimezoneId    идентификатор часового пояса клиента (например, "Europe/Moscow")
     * @return момент времени в UTC, или null если входные данные null
     */
    public static Instant toInstant(LocalDateTime clientLocalDateTime, String clientTimezoneId) {
        if (clientLocalDateTime == null || clientTimezoneId == null) {
            return null;
        }
        ZoneId zone = ZoneId.of(clientTimezoneId);
        return clientLocalDateTime.atZone(zone).toInstant();
    }

    /**
     * Преобразует момент времени в UTC в локальное время клиента по его часовому поясу.
     *
     * @param instant          момент времени в UTC
     * @param clientTimezoneId идентификатор часового пояса клиента
     * @return локальное время клиента, или null если входные данные null
     */
    public static LocalDateTime toLocalDateTime(Instant instant, String clientTimezoneId) {
        if (instant == null || clientTimezoneId == null) {
            return null;
        }
        ZoneId zone = ZoneId.of(clientTimezoneId);
        return instant.atZone(zone).toLocalDateTime();
    }

    /**
     * Вспомогательный метод: ZonedDateTime → Instant (редко нужен, но для полноты).
     */
    public static Instant toInstant(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.toInstant() : null;
    }

    /**
     * Вспомогательный метод: Instant → ZonedDateTime в указанном поясе.
     */
    public static ZonedDateTime toZonedDateTime(Instant instant, String timezoneId) {
        if (instant == null || timezoneId == null) {
            return null;
        }
        return instant.atZone(ZoneId.of(timezoneId));
    }
}

