package com.maverick.flightadminservice.flight.mappers;

import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.flight.dtos.FlightResponse;
import com.maverick.flightadminservice.flight.entities.Flight;

public final class FlightMapper {

    private FlightMapper() {
    }

    public static FlightResponse toResponse(Flight flight) {
        if (flight == null) {
            return null;
        }

        Airport origin = flight.getOriginAirport();
        Airport destination = flight.getDestinationAirport();

        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                origin != null ? origin.getId() : null,
                origin != null ? origin.getIataCode() : null,
                destination != null ? destination.getId() : null,
                destination != null ? destination.getIataCode() : null,
                flight.getScheduledDeparture(),
                flight.getScheduledArrival(),
                flight.getStatus()
        );
    }
}
