package ru.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.entity.User;
import ru.entity.dto.LocationsResponse;
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
            return "redirect:/error";
        }
        User user = (User) request.getAttribute("user");
        model.addAttribute("username", user.getLogin());

        return "search";
    }

    @PostMapping("/search")
    public String processSearch(Model model,
                                @RequestParam(name = "cityName") String cityName) {
        List<LocationsResponse> locations = openWeatherService.getLocationsByCityName(cityName);
        model.addAttribute("locations", locations);
        return "search";
    }
}
