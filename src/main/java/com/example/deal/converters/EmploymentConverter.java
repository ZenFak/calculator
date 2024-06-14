package com.example.deal.converters;

import com.example.deal.models.Employment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Converter(autoApply = true)
public class EmploymentConverter implements AttributeConverter<Employment, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Employment employment) {
        try {
            return objectMapper.writeValueAsString(employment);
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка преобразования в JSON", e);
        }
    }

    @Override
    public Employment convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, Employment.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка конвертора", e);
        }
    }
}