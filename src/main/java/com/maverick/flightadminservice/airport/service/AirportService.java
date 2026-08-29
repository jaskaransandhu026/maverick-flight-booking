package com.maverick.flightadminservice.airport.service;


import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;

import java.util.List;

public interface AirportService {

    AirportResponse createAirport(CreateAirportRequest request);

    AirportResponse getAirport(Long id);

    List<AirportResponse> getAirports();
}
