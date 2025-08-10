package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.entity.Location;
import ru.entity.User;
import ru.entity.dto.LocationsResponse;
import ru.entity.dto.WeatherResponse;
import ru.exception.DuplicateLocationException;
import ru.repository.LocationRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        String locationName = locationsResponse.name();
        if (locationsResponse.country() != null && !locationsResponse.country().isEmpty()) {
            locationName += ", " + locationsResponse.country();
        }
        Location location = Location.builder()
                .user(user)
                .name(locationName)
                .latitude(locationsResponse.lat())
                .longitude(locationsResponse.lon())
                .build();
        if (locationRepository.existsByNameAndUser(locationName, user)) {
            throw new DuplicateLocationException("Location already exists: " + locationName);
        }
        locationRepository.save(location);
    }

    @Transactional
    public List<Location> findByUser(User user) {
        return locationRepository.findByUser(user);
    }

    public List<WeatherResponse> getWeatherForLocations(List<Location> locations) {
        List<CompletableFuture<WeatherResponse>> futures = locations.stream()
                .map(location -> CompletableFuture.supplyAsync(() ->
                        openWeatherService.getWeatherByCoordinates(location.getLatitude(), location.getLongitude())))
                .toList();
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public boolean existsById(int locationId) {
        return locationRepository.existsById(locationId);
    }

    public void deleteById(int locationId) {
        locationRepository.deleteById(locationId);
    }
}
