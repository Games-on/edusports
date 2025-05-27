package com.example.checkscam.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule
        mapper.registerModule(new JavaTimeModule());
        
        // Custom module for LocalDateTime with ISO format support
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(LocalDateTime.class, new IsoLocalDateTimeDeserializer());
        mapper.registerModule(customModule);
        
        // Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Allow unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return mapper;
    }
    
    /**
     * Custom deserializer to handle ISO datetime strings with timezone info
     * Converts formats like "2025-05-29T04:05:00.000Z" to LocalDateTime
     */
    public static class IsoLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();
            
            try {
                // Try to parse as OffsetDateTime first (handles Z timezone)
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);
                return offsetDateTime.toLocalDateTime();
            } catch (Exception e) {
                try {
                    // Fallback to direct LocalDateTime parsing
                    return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception e2) {
                    throw new IOException("Unable to parse date: " + dateString, e2);
                }
            }
        }
    }
}
