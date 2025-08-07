package ru.entity.dto;

import java.math.BigDecimal;

public record LocationsResponse(
    String name,
    String country,
    String state,
    BigDecimal lat,
    BigDecimal lon) {
}
