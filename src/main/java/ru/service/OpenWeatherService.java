package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.entity.dto.LocationsResponse;
import ru.entity.dto.WeatherResponse;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OpenWeatherService {
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiLocationsUrl;
    private final String apiWeatherUrl;

    @Autowired
    public OpenWeatherService(RestTemplate restTemplate,
                              @Value("${openweather.api.key}") String apiKey,
                              @Value("${openweather.api.locations.url}") String apiLocationsUrl,
                              @Value("${openweather.api.weather.url}") String apiWeatherUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiLocationsUrl = apiLocationsUrl;
        this.apiWeatherUrl = apiWeatherUrl;
    }

    public List<LocationsResponse> getLocationsByCityName(String cityName) {
        String queryUrl = apiLocationsUrl.formatted(cityName, apiKey);

        LocationsResponse[] response = restTemplate.getForObject(queryUrl, LocationsResponse[].class);

        if (response == null || response.length == 0) {
            return List.of();
        }
        return List.of(response);
    }

    public WeatherResponse getWeatherByCoordinates(BigDecimal latitude, BigDecimal longitude) {
        String queryUrl = apiWeatherUrl.formatted(latitude, longitude, apiKey);

        return restTemplate.getForObject(queryUrl, WeatherResponse.class);
    }

}
