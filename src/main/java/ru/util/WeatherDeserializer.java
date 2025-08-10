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

        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode mainNode = node.get("main");
        JsonNode weatherNode = node.get("weather").get(0);

        int temp = mainNode.get("temp").intValue();
        int feelsLike = mainNode.get("feels_like").intValue();
        int humidity = mainNode.get("humidity").intValue();
        BigDecimal windSpeed = node.get("wind").get("speed").decimalValue();
        String weatherDescription = weatherNode.get("description").asText();
        String icon = weatherNode.get("icon").asText();

        return new WeatherResponse(temp, feelsLike, humidity, windSpeed, weatherDescription, icon);
    }

}
