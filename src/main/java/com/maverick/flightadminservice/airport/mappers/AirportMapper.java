package com.maverick.flightadminservice.airport.mappers;

import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.entities.Airport;

public final class AirportMapper {

    private AirportMapper() {
    }

    public static AirportResponse toResponse(Airport airport) {
        if (airport == null) {
            return null;
        }

        return new AirportResponse(
                airport.getId(),
                airport.getIataCode(),
                airport.getIcaoCode(),
                airport.getName(),
                airport.getCity(),
                airport.getCountryCode(),
                airport.getTimezone(),
                airport.isActive()
        );
    }
}
