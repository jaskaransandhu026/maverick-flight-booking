package com.maverick.flightadminservice.airport.controllers;

import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;
import com.maverick.flightadminservice.airport.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AirportResponse createAirport(
            @Valid @RequestBody CreateAirportRequest request
    ) {
        return airportService.createAirport(request);
    }

    @GetMapping("/{id}")
    public AirportResponse getAirport(@PathVariable Long id) {
        return airportService.getAirport(id);
    }

    @GetMapping
    public List<AirportResponse> getAirports() {
        return airportService.getAirports();
    }
}
