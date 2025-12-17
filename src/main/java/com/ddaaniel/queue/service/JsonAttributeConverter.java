package com.ddaaniel.queue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddaaniel.queue.domain.model.dto.SettingsData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class JsonAttributeConverter implements AttributeConverter<SettingsData, String> {
    private static final Logger log = LoggerFactory.getLogger(JsonAttributeConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SettingsData settings) {
        try {
            return objectMapper.writeValueAsString(settings);
        } catch (JsonProcessingException jpe) {
            log.warn("Cannot convert into JSON");
            return null;
        }
    }

    @Override
    public SettingsData convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, SettingsData.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert JSON");
            return null;
        }
    }
}
