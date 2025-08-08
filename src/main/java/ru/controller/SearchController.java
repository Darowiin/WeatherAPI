package ru.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.entity.User;
import ru.entity.dto.CitySearchForm;
import ru.entity.dto.LocationsResponse;
import ru.exception.InvalidLocationDataException;
import ru.exception.LocationNotFoundException;
import ru.exception.SessionNotFoundException;
import ru.service.OpenWeatherService;

import java.util.List;

@Controller
public class SearchController {
    private final OpenWeatherService openWeatherService;

    @Autowired
    public SearchController(OpenWeatherService openWeatherService) {
        this.openWeatherService = openWeatherService;
    }

    @GetMapping("/search")
    public String getSearchPage(HttpServletRequest request, Model model) {
        if (request.getAttribute("user") == null) {
            throw new SessionNotFoundException("Session not found");
        }
        User user = (User) request.getAttribute("user");
        model.addAttribute("username", user.getLogin());

        return "search";
    }

    @PostMapping("/search")
    public String processSearch(HttpServletRequest request,
                                Model model,
                                @Valid CitySearchForm citySearchForm,
                                BindingResult bindingResult) {
        User user = (User) request.getAttribute("user");
        model.addAttribute("username", user.getLogin());

        if (bindingResult.hasErrors()) {
            throw new InvalidLocationDataException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        List<LocationsResponse> locations = openWeatherService.getLocationsByCityName(citySearchForm.cityName());

        if (locations.isEmpty()) {
            throw new LocationNotFoundException("No locations found for: " + citySearchForm.cityName());
        }

        model.addAttribute("locations", locations);
        return "search";
    }
}
