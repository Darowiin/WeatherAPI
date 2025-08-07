package ru.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.entity.dto.WeatherResponse;

import java.io.IOException;
import java.math.BigDecimal;

public class WeatherDeserializer extends JsonDeserializer<WeatherResponse> {

    @Override
    public WeatherResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode productNode = jp.getCodec().readTree(jp);

        int temp = productNode.get("main").get("temp").intValue();
        int feelsLike = productNode.get("main").get("feels_like").intValue();
        int humidity = productNode.get("main").get("humidity").intValue();
        BigDecimal windSpeed = productNode.get("wind").get("speed").decimalValue();
        String weatherDescription = productNode.get("weather").get(0).get("description").asText();

        return new WeatherResponse(temp, feelsLike, humidity, windSpeed, weatherDescription);
    }

}
