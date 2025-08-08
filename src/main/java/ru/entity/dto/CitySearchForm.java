package ru.entity.dto;

import jakarta.validation.constraints.NotBlank;

public record CitySearchForm(
        @NotBlank(message = "City name cannot be empty.")
        String cityName
) {
}