package com.maverick.flightadminservice.airport.service;

import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;
import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import com.maverick.flightadminservice.commons.exception.ConflictException;
import com.maverick.flightadminservice.commons.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AirportServiceImplTest {

    @Mock
    private AirportRepository airportRepository;

    private AirportServiceImpl airportService;

    @BeforeEach
    void setUp() {
        airportService = new AirportServiceImpl(airportRepository);
    }

    @Test
    void createAirport_shouldThrowConflict_whenIataCodeAlreadyExists() {

        // Arrange
        CreateAirportRequest request = new CreateAirportRequest(
                "AKL",
                "NZAA",
                "Auckland Airport",
                "Auckland",
                "NZ",
                "Pacific/Auckland"
        );

        // creating the mock for the repository layer
        when(
                airportRepository.existsByIataCode("AKL")
        ).thenReturn(true);

        // Act
        ConflictException exception =
                assertThrows(
                    ConflictException.class,
                    () -> airportService.createAirport(request)
                );


        // Assert
        assertEquals(
                "Airport with IATA code AKL already exists in the system.",
                exception.getMessage()
        );

        // verify that the service actually called the repository method
        verify(
                airportRepository
        ).existsByIataCode("AKL");
    }


    @Test
    void createAirport_shouldCreateAirport_whenRequestIsValid() {

        // Arrange
        CreateAirportRequest request =
                new CreateAirportRequest(
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland"
                );

        when(
                airportRepository.existsByIataCode("AKL")
        ).thenReturn(false);

        Airport airport =
                new Airport(
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland"
                );

        when(
                airportRepository.save(any(Airport.class))
        ).thenReturn(airport);


        // Act
        AirportResponse response =
                assertDoesNotThrow(
                        () -> airportService.createAirport(request)
                );


        // Assert
        AirportResponse expectedResponse =
                new AirportResponse(
                        null,
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland",
                        true
                );

        assertEquals(expectedResponse, response);

        verify(
                airportRepository
        ).existsByIataCode("AKL");

        verify(
                airportRepository
        ).save(any(Airport.class));
    }


    @Test
    void getAirport_shouldReturnAirport_whenAirportExists() {

        // Arrange
        Long airportId = 1L;

        Airport airport =
                new Airport(
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland"
                );

        when(
                airportRepository.findById(airportId)
        ).thenReturn(
                Optional.of(airport)
        );


        // Act
        AirportResponse response =
                assertDoesNotThrow(
                        () -> airportService.getAirport(airportId)
                );


        // Assert
        AirportResponse expectedResponse =
                new AirportResponse(
                        null,
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland",
                        true
                );

        assertEquals(expectedResponse, response);

        verify(
                airportRepository
        ).findById(airportId);
    }

    @Test
    void getAirport_shouldThrowNotFound_whenAirportDoesNotExist() {

        // Arrange
        Long airportId = 999L;

        when(
                airportRepository.findById(airportId)
        ).thenReturn(
                Optional.empty()
        );


        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> airportService.getAirport(airportId)
                );


        // Assert
        assertEquals(
                "Airport with id " + airportId + " not found",
                exception.getMessage()
        );

        verify(
                airportRepository
        ).findById(airportId);
    }


}
