package com.maverick.flightadminservice.flight.controllers;

import com.maverick.flightadminservice.flight.dtos.CreateFlightRequest;
import com.maverick.flightadminservice.flight.dtos.FlightResponse;
import com.maverick.flightadminservice.flight.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponse createFlight(
            @Valid @RequestBody CreateFlightRequest request){
        return flightService.createFlight(request);
    }

    @GetMapping("/{id}")
    public FlightResponse getFlight(
            @PathVariable Long id
    ) {
        return flightService.getFlight(id);
    }

    @GetMapping
    public List<FlightResponse> getFlights() {
        return flightService.getFlights();
    }


}
