package ru.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.entity.Location;
import ru.entity.User;
import ru.entity.dto.LocationsResponse;
import ru.entity.dto.WeatherResponse;
import ru.exception.SessionNotFoundException;
import ru.service.LocationService;
import ru.service.SessionService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class ApplicationController {
    private final SessionService sessionService;
    private final LocationService locationService;

    @Autowired
    public ApplicationController(SessionService sessionService, LocationService locationService) {
        this.sessionService = sessionService;
        this.locationService = locationService;
    }

    @RequestMapping("/error")
    public String getErrorPage() {
        return "error";
    }

    @GetMapping("/")
    public String getIndexPage(HttpServletRequest request, Model model) {
        User user = (User) request.getAttribute("user");

        if (user != null) {
            model.addAttribute("username", user.getLogin());
            List<Location> locations = locationService.findByUser(user);
            List<WeatherResponse> weatherResponses = locationService.getWeatherForLocations(locations);

            model.addAttribute("locations", locations);
            model.addAttribute("weatherResponses", weatherResponses);
        }

        return "index";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Arrays.stream(request.getCookies())
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .map(UUID::fromString).ifPresent(sessionService::invalidateSession);
        Cookie cookie = new Cookie("sessionId", null);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @PostMapping("/")
    public String addLocation(HttpServletRequest request,
                              @ModelAttribute LocationsResponse locationsResponse) {
        if (request.getAttribute("user") == null) {
            throw new SessionNotFoundException("Session not found");
        }
        User user = (User) request.getAttribute("user");

        locationService.save(locationsResponse, user);

        return "redirect:/";
    }

    @PostMapping("/delete-location")
    public String deleteLocationPost(HttpServletRequest request,
                                     @RequestParam("locationId") int locationId) {
        if (request.getAttribute("user") == null) {
            throw new SessionNotFoundException("Session not found");
        }

        if (locationService.existsById(locationId)) {
            locationService.deleteById(locationId);
        }

        return "redirect:/";
    }
}
