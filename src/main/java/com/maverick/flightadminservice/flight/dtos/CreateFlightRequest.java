package com.maverick.flightadminservice.flight.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CreateFlightRequest(

        @NotBlank
        @Size(max = 10)
        String flightNumber,

        @NotNull
        Long originAirportId,

        @NotNull
        Long destinationAirportId,

        @NotNull
        @Future
        OffsetDateTime scheduledDeparture,

        @NotNull
        @Future
        OffsetDateTime scheduledArrival
) {
}
