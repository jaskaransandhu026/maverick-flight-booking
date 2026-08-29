package com.maverick.flightadminservice.airport.service;

import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;
import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.airport.mappers.AirportMapper;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AirportServiceImpl implements AirportService{

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

        //check if airport already exists using the airport code
        String iataCode = request.iataCode().trim().toUpperCase(Locale.ROOT);

        if (airportRepository.existsByIataCode(iataCode)) {
            throw new IllegalArgumentException("Airport with IATA code " + iataCode + " already exists in the system.");
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

        return AirportMapper.toResponse(savedAirport);
    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public AirportResponse getAirport(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Airport with id " + id + " not found"
                        )
                );

        return AirportMapper.toResponse(airport);
    }

    /**
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<AirportResponse> getAirports() {

        return airportRepository.findAll()
                .stream()
                .map(AirportMapper::toResponse)
                .toList();
    }
}
