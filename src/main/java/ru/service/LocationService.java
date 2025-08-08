package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.entity.Location;
import ru.entity.User;
import ru.entity.dto.LocationsResponse;
import ru.entity.dto.WeatherResponse;
import ru.repository.LocationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final OpenWeatherService openWeatherService;

    @Autowired
    public LocationService(LocationRepository locationRepository, OpenWeatherService openWeatherService) {
        this.locationRepository = locationRepository;
        this.openWeatherService = openWeatherService;
    }

    @Transactional
    public void save(LocationsResponse locationsResponse, User user) {
        Location location = Location.builder()
                .name(locationsResponse.name() + ", " + locationsResponse.country())
                .user(user)
                .latitude(locationsResponse.lat())
                .longitude(locationsResponse.lon())
                .build();
        locationRepository.save(location);
    }

    @Transactional
    public List<Location> findByUser(User user) {
        return locationRepository.findByUser(user);
    }

    public List<WeatherResponse> getWeatherForLocations(List<Location> locations) {
        List<WeatherResponse> weatherResponses = new ArrayList<>();
        for (Location location: locations) {
            WeatherResponse weatherResponse = openWeatherService.getWeatherByCoordinates(location.getLatitude(), location.getLongitude());
            weatherResponses.add(weatherResponse);
        }
        return weatherResponses;
    }

    public boolean existsById(int locationId) {
        return locationRepository.existsById(locationId);
    }

    public void deleteById(int locationId) {
        locationRepository.deleteById(locationId);
    }
}
