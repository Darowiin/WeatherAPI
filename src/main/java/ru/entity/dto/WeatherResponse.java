package ru.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.util.WeatherDeserializer;

import java.math.BigDecimal;

@JsonDeserialize(using = WeatherDeserializer.class)
public record WeatherResponse(
        int temp,
        int feelsLike,
        int humidity,
        BigDecimal windSpeed,
        String weatherDescription,
        String icon
) {
}
