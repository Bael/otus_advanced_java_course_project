package com.github.bael.otus.java.calendar.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalTime;

public class CustomLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        // Если приходит объект
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            JsonNode node = p.getCodec().readTree(p);

            // Пытаемся извлечь время из объекта
            if (node.has("hour") && node.has("minute")) {
                int hour = node.get("hour").asInt();
                int minute = node.get("minute").asInt();
                int second = node.has("second") ? node.get("second").asInt() : 0;
                int nano = node.has("nano") ? node.get("nano").asInt() : 0;

                return LocalTime.of(hour, minute, second, nano);
            }
        }

        // Если приходит строка
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            return LocalTime.parse(p.getValueAsString());
        }

        throw new IOException("Cannot deserialize LocalTime");
    }
}