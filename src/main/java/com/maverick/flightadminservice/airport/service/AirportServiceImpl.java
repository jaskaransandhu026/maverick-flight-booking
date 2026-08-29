package com.maverick.flightadminservice.airport.service;

import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;
import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.airport.mappers.AirportMapper;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import com.maverick.flightadminservice.commons.exception.ConflictException;
import com.maverick.flightadminservice.commons.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AirportServiceImpl implements AirportService{

    private static final Logger log = LoggerFactory.getLogger(AirportServiceImpl.class);

    private final AirportRepository airportRepository;

    public AirportServiceImpl(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    /**
     * @param request
     * @return
     */
    @Override
    @Transactional
    public AirportResponse createAirport(CreateAirportRequest request) {
        log.info("Creating airport with request payload");

        if (request == null) {
            throw new ResourceNotFoundException("Airport request does not exist.");
        }

        //check if airport already exists using the airport code
        String iataCode = request.iataCode().trim().toUpperCase(Locale.ROOT);

        if (airportRepository.existsByIataCode(iataCode)) {
            throw new ConflictException("Airport with IATA code " + iataCode + " already exists in the system.");
        }

        // create and register the new airport
        Airport airport = new Airport(
                iataCode,
                request.icaoCode().trim().toUpperCase(Locale.ROOT),
                request.name().trim(),
                request.city().trim(),
                request.countryCode().trim().toUpperCase(Locale.ROOT),
                request.timezone().trim()
        );

        Airport savedAirport = airportRepository.save(airport);
        log.info("Airport created successfully: id={}, iataCode={}, icaoCode={}, city={}, countryCode={}",
                savedAirport.getId(),
                savedAirport.getIataCode(),
                savedAirport.getIcaoCode(),
                savedAirport.getCity(),
                savedAirport.getCountryCode());

        return AirportMapper.toResponse(savedAirport);
    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public AirportResponse getAirport(Long id) {
        log.debug("Fetching airport by id: {}", id);

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Airport with id " + id + " not found"
                ));

        log.debug("Airport fetched successfully: id={}, iataCode={}", airport.getId(), airport.getIataCode());
        return AirportMapper.toResponse(airport);
    }

    /**
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<AirportResponse> getAirports() {
        log.debug("Fetching all airports");

        List<AirportResponse> airports = airportRepository.findAll()
                .stream()
                .map(AirportMapper::toResponse)
                .toList();

        log.debug("Fetched airports count: {}", airports.size());
        return airports;
    }
}
