package com.example.deal.converters;

import com.example.deal.models.StatusHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Converter(autoApply = true)
public class StatusHistoryConverter implements AttributeConverter<StatusHistory, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(StatusHistory statusHistory) {
        try {
            return objectMapper.writeValueAsString(statusHistory);
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка преобразования в JSON", e);
        }
    }

    @Override
    public StatusHistory convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, StatusHistory.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка конвертора", e);
        }
    }
}
