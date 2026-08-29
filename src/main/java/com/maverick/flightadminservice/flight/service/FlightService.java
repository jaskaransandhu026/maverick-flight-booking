package com.maverick.flightadminservice.flight.service;

import com.maverick.flightadminservice.flight.dtos.CreateFlightRequest;
import com.maverick.flightadminservice.flight.dtos.FlightResponse;

import java.util.List;

public interface FlightService {

    FlightResponse createFlight(CreateFlightRequest request);

    FlightResponse getFlight(Long id);

    List<FlightResponse> getFlights();
}
