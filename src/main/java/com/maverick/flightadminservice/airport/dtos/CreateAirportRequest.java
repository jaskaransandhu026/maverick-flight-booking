package com.maverick.flightadminservice.airport.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAirportRequest(

        @NotBlank
        @Size(min = 3, max = 3)
        String iataCode,

        @NotBlank
        @Size(min = 4, max = 4)
        String icaoCode,

        @NotBlank
        String name,

        @NotBlank
        String city,

        @NotBlank
        @Size(min = 2, max = 2)
        String countryCode,

        @NotBlank
        String timezone
) {
}
