package com.maverick.flightadminservice.flight.dtos;

import com.maverick.flightadminservice.flight.enums.FlightStatus;

import java.time.OffsetDateTime;

public record FlightResponse(

        Long id,

        String flightNumber,

        Long originAirportId,
        String originAirportCode,

        Long destinationAirportId,
        String destinationAirportCode,

        OffsetDateTime scheduledDeparture,
        OffsetDateTime scheduledArrival,

        FlightStatus status
) {
}
