package com.maverick.flightadminservice.flight.service;

import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import com.maverick.flightadminservice.commons.exception.BadRequestException;
import com.maverick.flightadminservice.commons.exception.ConflictException;
import com.maverick.flightadminservice.commons.exception.ResourceNotFoundException;
import com.maverick.flightadminservice.flight.dtos.CreateFlightRequest;
import com.maverick.flightadminservice.flight.dtos.FlightResponse;
import com.maverick.flightadminservice.flight.entities.Flight;
import com.maverick.flightadminservice.flight.mappers.FlightMapper;
import com.maverick.flightadminservice.flight.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class FlightServiceImpl implements FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;


    public FlightServiceImpl(FlightRepository flightRepository, AirportRepository airportRepository){
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
    }

    /**
     * @param request
     * @return
     */
    @Override
    @Transactional
    public FlightResponse createFlight(CreateFlightRequest request) {
        log.info("Creating flight with request payload");

        // check that the createflight isnt null
        if (request == null){
            throw new ResourceNotFoundException("Flight Request doesnt exist.");
        }

        // check that the to and from airports exist
        Airport originAirport = airportRepository.findById(request.originAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin Airport not found."));

        Airport destinationAirport = airportRepository.findById(request.destinationAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Airport not found."));

        // check that the to and from airports aren't the same
        if (originAirport.equals(destinationAirport)){
            throw new BadRequestException("Origin and destination airports are the same");
        }

        // check that the scheduled arrival is before the scheduled departure
        if (!request.scheduledArrival().isAfter(request.scheduledDeparture())) {
            throw new BadRequestException(
                    "Scheduled arrival must be after scheduled departure"
            );
        }

        // check that the flight doesnt already exist
        String flightNumber = request.flightNumber().trim().toUpperCase(Locale.ROOT);

        if (flightRepository.existsByFlightNumberAndScheduledDeparture(flightNumber,request.scheduledDeparture())){
            throw new ConflictException("Flight is already scheduled for this departure time");
        }

        // create the flight
        Flight flight = new Flight(flightNumber, originAirport, destinationAirport, request.scheduledDeparture(), request.scheduledArrival());
        flightRepository.save(flight);

        log.info("Flight created successfully: id={}, flightNumber={}, originAirportId={}, destinationAirportId={}, departure={}, arrival={}",
                flight.getId(),
                flight.getFlightNumber(),
                originAirport.getId(),
                destinationAirport.getId(),
                flight.getScheduledDeparture(),
                flight.getScheduledArrival());

        // call the mapper to return the flight response

        return FlightMapper.toResponse(flight);
    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public FlightResponse getFlight(Long id) {
        log.debug("Fetching flight by id: {}", id);

        Flight flight = flightRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flight with id " + id + " not found"));

        log.debug("Flight fetched successfully: id={}, flightNumber={}", flight.getId(), flight.getFlightNumber());
        return FlightMapper.toResponse(flight);
    }

    /**
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<FlightResponse> getFlights() {
        log.debug("Fetching all flights");
        List<FlightResponse> flights = flightRepository.findAll().stream().map(FlightMapper::toResponse).toList();
        log.debug("Fetched flights count: {}", flights.size());
        return flights;
    }
}
