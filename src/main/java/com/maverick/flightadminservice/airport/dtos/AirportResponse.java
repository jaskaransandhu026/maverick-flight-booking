package com.maverick.flightadminservice.airport.dtos;

public record AirportResponse(
        Long id,
        String iataCode,
        String icaoCode,
        String name,
        String city,
        String countryCode,
        String timezone,
        boolean active
) {
}
