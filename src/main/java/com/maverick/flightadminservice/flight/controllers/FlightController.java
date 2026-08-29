package com.maverick.flightadminservice.flight.controllers;

import com.maverick.flightadminservice.flight.dtos.CreateFlightRequest;
import com.maverick.flightadminservice.flight.dtos.FlightResponse;
import com.maverick.flightadminservice.flight.service.FlightService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);

    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponse createFlight(
            @Valid @RequestBody CreateFlightRequest request){
        log.info("Received create flight request: flightNumber={}, originAirportId={}, destinationAirportId={}, scheduledDeparture={}",
                request != null ? request.flightNumber() : null,
                request != null ? request.originAirportId() : null,
                request != null ? request.destinationAirportId() : null,
                request != null ? request.scheduledDeparture() : null);
        return flightService.createFlight(request);
    }

    @GetMapping("/{id}")
    public FlightResponse getFlight(
            @PathVariable Long id
    ) {
        log.debug("Received get flight request for id={}", id);
        return flightService.getFlight(id);
    }

    @GetMapping
    public List<FlightResponse> getFlights() {
        log.debug("Received get all flights request");
        return flightService.getFlights();
    }


}
