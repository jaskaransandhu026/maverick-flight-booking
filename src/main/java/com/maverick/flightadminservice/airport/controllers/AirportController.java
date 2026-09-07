package com.maverick.flightadminservice.airport.controllers;

import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;
import com.maverick.flightadminservice.airport.service.AirportService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/airports")
public class AirportController {

    private static final Logger log = LoggerFactory.getLogger(AirportController.class);

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AirportResponse createAirport(
            @Valid @RequestBody CreateAirportRequest request
    ) {
        log.info("Received create airport request: iataCode={}, icaoCode={}, city={}, countryCode={}",
                request != null ? request.iataCode() : null,
                request != null ? request.icaoCode() : null,
                request != null ? request.city() : null,
                request != null ? request.countryCode() : null);
        
        return airportService.createAirport(request);
    }

    @GetMapping("/{id}")
    public AirportResponse getAirport(@PathVariable Long id) {
        log.debug("Received get airport request for id={}", id);
        return airportService.getAirport(id);
    }

    @GetMapping
    public List<AirportResponse> getAirports() {
        log.debug("Received get all airports request");
        return airportService.getAirports();
    }
}
