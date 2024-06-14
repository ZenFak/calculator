package com.example.deal.converters;

import com.example.deal.models.Passport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Converter(autoApply = true)
public class PassportConverter implements AttributeConverter<Passport, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Passport passportData) {
        try {
            return objectMapper.writeValueAsString(passportData);
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка преобразования в JSON", e);
        }
    }

    @Override
    public Passport convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, Passport.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка конвертора", e);
        }
    }
}
