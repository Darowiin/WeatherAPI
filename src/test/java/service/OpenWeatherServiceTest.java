package service;

import config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.entity.dto.LocationsResponse;
import ru.entity.dto.WeatherResponse;
import ru.service.OpenWeatherService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenWeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenWeatherService openWeatherService;

    private final String API_KEY;
    private final String API_LOCATIONS_URL;
    private final String API_WEATHER_URL;

    @Autowired
    public OpenWeatherServiceTest(@Value("${openweather.api.key}") String apiKey,
                                  @Value("${openweather.api.locations.url}") String apiLocationsUrl,
                                  @Value("${openweather.api.weather.url}") String apiWeatherUrl) {
        API_KEY = apiKey;
        API_LOCATIONS_URL = apiLocationsUrl;
        API_WEATHER_URL = apiWeatherUrl;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        openWeatherService = new OpenWeatherService(restTemplate, API_KEY, API_LOCATIONS_URL, API_WEATHER_URL);
    }

    @Test
    public void getLocationsByCityName_ReturnsCorrectLocations() {
        // given
        String cityName = "Москва";
        LocationsResponse[] mockResponse = new LocationsResponse[] {
                new LocationsResponse("Москва", "RU", null, new BigDecimal("55.7522"), new BigDecimal("37.6156"))
        };

        when(restTemplate.getForObject(anyString(), eq(LocationsResponse[].class))).thenReturn(mockResponse);

        // when
        List<LocationsResponse> result = openWeatherService.getLocationsByCityName(cityName);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Москва", result.get(0).name());
        assertEquals("RU", result.get(0).country());
        assertEquals(new BigDecimal("55.7522"), result.get(0).lat());
    }

    @Test
    public void getLocationsByCityName_EmptyResponse_ReturnsEmptyList() {
        // given
        when(restTemplate.getForObject(anyString(), eq(LocationsResponse[].class))).thenReturn(new LocationsResponse[0]);

        // when
        List<LocationsResponse> result = openWeatherService.getLocationsByCityName("НесуществующийГород");

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void getLocationsByCityName_NullResponse_ReturnsEmptyList() {
        // given
        when(restTemplate.getForObject(anyString(), eq(LocationsResponse[].class))).thenReturn(null);

        // when
        List<LocationsResponse> result = openWeatherService.getLocationsByCityName("НесуществующийГород");

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void getLocationsByCityName_ClientError_ThrowsException() {
        // given
        when(restTemplate.getForObject(anyString(), eq(LocationsResponse[].class)))
                .thenThrow(HttpClientErrorException.BadRequest.create(HttpStatusCode.valueOf(400), "Bad Request",
                        null, null, null));

        // then
        assertThrows(HttpClientErrorException.class,
                () -> openWeatherService.getLocationsByCityName("ErrorCity"));
    }

    @Test
    public void getLocationsByCityName_ServerError_ThrowsException() {
        // given
        when(restTemplate.getForObject(anyString(), eq(LocationsResponse[].class)))
                .thenThrow(HttpServerErrorException.InternalServerError.create(HttpStatusCode.valueOf(500), "Internal Server Error",
                        null, null, null));

        // then
        assertThrows(HttpServerErrorException.class,
                () -> openWeatherService.getLocationsByCityName("ErrorCity"));
    }

    @Test
    public void getWeatherByCoordinates_ReturnsCorrectWeather() {
        // given
        BigDecimal lat = new BigDecimal("55.7522");
        BigDecimal lon = new BigDecimal("37.6156");
        WeatherResponse mockWeather = new WeatherResponse(20, 20,
                71, BigDecimal.valueOf(4.27),
                "overcast clouds",
                "04d");

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(mockWeather);

        // when
        WeatherResponse result = openWeatherService.getWeatherByCoordinates(lat, lon);

        // then
        assertNotNull(result);
    }

    @Test
    public void getWeatherByCoordinates_ClientError_ThrowsException() {
        // given
        BigDecimal lat = new BigDecimal("999.999");
        BigDecimal lon = new BigDecimal("999.999");

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
                .thenThrow(HttpClientErrorException.BadRequest.create(HttpStatusCode.valueOf(400), "Bad Request", null, null, null));

        // then
        assertThrows(HttpClientErrorException.class,
                () -> openWeatherService.getWeatherByCoordinates(lat, lon));
    }
}