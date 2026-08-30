package com.maverick.flightadminservice.flight.service;

import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import com.maverick.flightadminservice.commons.exception.BadRequestException;
import com.maverick.flightadminservice.commons.exception.ConflictException;
import com.maverick.flightadminservice.commons.exception.ResourceNotFoundException;
import com.maverick.flightadminservice.flight.dtos.CreateFlightRequest;
import com.maverick.flightadminservice.flight.dtos.FlightResponse;
import com.maverick.flightadminservice.flight.entities.Flight;
import com.maverick.flightadminservice.flight.repository.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    private FlightServiceImpl flightService;


    @BeforeEach
    void setUp() {

        flightService =
                new FlightServiceImpl(
                        flightRepository,
                        airportRepository
                );
    }


    @Test
    void createFlight_shouldThrowNotFound_whenRequestIsNull() {

        // Arrange
        CreateFlightRequest request = null;


        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Flight Request doesnt exist.",
                exception.getMessage()
        );

        verifyNoInteractions(
                airportRepository,
                flightRepository
        );
    }


    @Test
    void createFlight_shouldThrowNotFound_whenOriginAirportDoesNotExist() {

        // Arrange
        CreateFlightRequest request =
                createValidFlightRequest();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.empty()
        );


        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Origin Airport not found.",
                exception.getMessage()
        );

        verify(
                airportRepository
        ).findById(1L);

        verify(
                airportRepository,
                never()
        ).findById(2L);

        verifyNoInteractions(
                flightRepository
        );
    }


    @Test
    void createFlight_shouldThrowNotFound_whenDestinationAirportDoesNotExist() {

        // Arrange
        CreateFlightRequest request =
                createValidFlightRequest();

        Airport originAirport =
                createAucklandAirport();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.of(originAirport)
        );

        when(
                airportRepository.findById(2L)
        ).thenReturn(
                Optional.empty()
        );


        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Destination Airport not found.",
                exception.getMessage()
        );

        verify(
                airportRepository
        ).findById(1L);

        verify(
                airportRepository
        ).findById(2L);

        verifyNoInteractions(
                flightRepository
        );
    }


    @Test
    void createFlight_shouldThrowBadRequest_whenOriginAndDestinationAreSame() {

        // Arrange
        CreateFlightRequest request =
                createValidFlightRequest();

        Airport airport =
                createAucklandAirport();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.of(airport)
        );

        /*
         * Deliberately return the SAME Airport object.
         */
        when(
                airportRepository.findById(2L)
        ).thenReturn(
                Optional.of(airport)
        );


        // Act
        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Origin and destination airports are the same",
                exception.getMessage()
        );

        verifyNoInteractions(
                flightRepository
        );
    }


    @Test
    void createFlight_shouldThrowBadRequest_whenArrivalIsBeforeDeparture() {

        // Arrange
        OffsetDateTime departure =
                OffsetDateTime.parse(
                        "2026-11-10T15:00:00Z"
                );

        OffsetDateTime arrival =
                OffsetDateTime.parse(
                        "2026-11-10T14:00:00Z"
                );

        CreateFlightRequest request =
                new CreateFlightRequest(
                        "NZ101",
                        1L,
                        2L,
                        departure,
                        arrival
                );

        Airport originAirport =
                createAucklandAirport();

        Airport destinationAirport =
                createSydneyAirport();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.of(originAirport)
        );

        when(
                airportRepository.findById(2L)
        ).thenReturn(
                Optional.of(destinationAirport)
        );


        // Act
        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Scheduled arrival must be after scheduled departure",
                exception.getMessage()
        );

        verifyNoInteractions(
                flightRepository
        );
    }


    @Test
    void createFlight_shouldThrowBadRequest_whenArrivalEqualsDeparture() {

        // Arrange
        OffsetDateTime departure =
                OffsetDateTime.parse(
                        "2026-11-10T15:00:00Z"
                );

        OffsetDateTime arrival =
                OffsetDateTime.parse(
                        "2026-11-10T15:00:00Z"
                );

        CreateFlightRequest request =
                new CreateFlightRequest(
                        "NZ101",
                        1L,
                        2L,
                        departure,
                        arrival
                );

        Airport originAirport =
                createAucklandAirport();

        Airport destinationAirport =
                createSydneyAirport();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.of(originAirport)
        );

        when(
                airportRepository.findById(2L)
        ).thenReturn(
                Optional.of(destinationAirport)
        );


        // Act
        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Scheduled arrival must be after scheduled departure",
                exception.getMessage()
        );

        verifyNoInteractions(
                flightRepository
        );
    }


    @Test
    void createFlight_shouldThrowConflict_whenFlightAlreadyExists() {

        // Arrange
        CreateFlightRequest request =
                createValidFlightRequest();

        Airport originAirport =
                createAucklandAirport();

        Airport destinationAirport =
                createSydneyAirport();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.of(originAirport)
        );

        when(
                airportRepository.findById(2L)
        ).thenReturn(
                Optional.of(destinationAirport)
        );

        when(
                flightRepository
                        .existsByFlightNumberAndScheduledDeparture(
                                "NZ101",
                                request.scheduledDeparture()
                        )
        ).thenReturn(true);


        // Act
        ConflictException exception =
                assertThrows(
                        ConflictException.class,
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "Flight is already scheduled for this departure time",
                exception.getMessage()
        );

        verify(
                flightRepository
        ).existsByFlightNumberAndScheduledDeparture(
                "NZ101",
                request.scheduledDeparture()
        );

        verify(
                flightRepository,
                never()
        ).save(any(Flight.class));
    }


    @Test
    void createFlight_shouldCreateFlight_whenRequestIsValid() {

        // Arrange
        CreateFlightRequest request =
                new CreateFlightRequest(
                        "nz101",
                        1L,
                        2L,
                        OffsetDateTime.parse(
                                "2026-11-10T08:00:00+13:00"
                        ),
                        OffsetDateTime.parse(
                                "2026-11-10T10:10:00+11:00"
                        )
                );

        Airport originAirport =
                createAucklandAirport();

        Airport destinationAirport =
                createSydneyAirport();

        when(
                airportRepository.findById(1L)
        ).thenReturn(
                Optional.of(originAirport)
        );

        when(
                airportRepository.findById(2L)
        ).thenReturn(
                Optional.of(destinationAirport)
        );

        when(
                flightRepository
                        .existsByFlightNumberAndScheduledDeparture(
                                "NZ101",
                                request.scheduledDeparture()
                        )
        ).thenReturn(false);


        // Act
        FlightResponse response =
                assertDoesNotThrow(
                        () -> flightService.createFlight(request)
                );


        // Assert
        assertEquals(
                "NZ101",
                response.flightNumber()
        );

        assertEquals(
                "AKL",
                response.originAirportCode()
        );

        assertEquals(
                "SYD",
                response.destinationAirportCode()
        );

        assertEquals(
                request.scheduledDeparture(),
                response.scheduledDeparture()
        );

        assertEquals(
                request.scheduledArrival(),
                response.scheduledArrival()
        );

        assertEquals(
                "SCHEDULED",
                response.status().name()
        );


        /*
         * Capture the Flight passed into save()
         * so we can inspect exactly what the service created.
         */
        ArgumentCaptor<Flight> flightCaptor =
                ArgumentCaptor.forClass(
                        Flight.class
                );

        verify(
                flightRepository
        ).save(
                flightCaptor.capture()
        );

        Flight savedFlight =
                flightCaptor.getValue();


        /*
         * This also proves that "nz101"
         * was normalized into "NZ101".
         */
        assertEquals(
                "NZ101",
                savedFlight.getFlightNumber()
        );

        assertEquals(
                originAirport,
                savedFlight.getOriginAirport()
        );

        assertEquals(
                destinationAirport,
                savedFlight.getDestinationAirport()
        );

        assertEquals(
                request.scheduledDeparture(),
                savedFlight.getScheduledDeparture()
        );

        assertEquals(
                request.scheduledArrival(),
                savedFlight.getScheduledArrival()
        );
    }


    @Test
    void getFlight_shouldReturnFlight_whenFlightExists() {

        // Arrange
        Long flightId = 1L;

        Airport originAirport =
                createAucklandAirport();

        Airport destinationAirport =
                createSydneyAirport();

        Flight flight =
                new Flight(
                        "NZ101",
                        originAirport,
                        destinationAirport,
                        OffsetDateTime.parse(
                                "2026-11-10T08:00:00+13:00"
                        ),
                        OffsetDateTime.parse(
                                "2026-11-10T10:10:00+11:00"
                        )
                );

        when(
                flightRepository.findById(flightId)
        ).thenReturn(
                Optional.of(flight)
        );


        // Act
        FlightResponse response =
                assertDoesNotThrow(
                        () -> flightService.getFlight(flightId)
                );


        // Assert
        assertEquals(
                "NZ101",
                response.flightNumber()
        );

        assertEquals(
                "AKL",
                response.originAirportCode()
        );

        assertEquals(
                "SYD",
                response.destinationAirportCode()
        );

        verify(
                flightRepository
        ).findById(flightId);
    }


    @Test
    void getFlight_shouldThrowException_whenFlightDoesNotExist() {

        // Arrange
        Long flightId = 999L;

        when(
                flightRepository.findById(flightId)
        ).thenReturn(
                Optional.empty()
        );


        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> flightService.getFlight(flightId)
                );


        // Assert
        assertEquals(
                "Flight with id 999 not found",
                exception.getMessage()
        );

        verify(
                flightRepository
        ).findById(flightId);
    }


    @Test
    void getFlights_shouldReturnAllFlights() {

        // Arrange
        Airport originAirport =
                createAucklandAirport();

        Airport destinationAirport =
                createSydneyAirport();

        Flight flightOne =
                new Flight(
                        "NZ101",
                        originAirport,
                        destinationAirport,
                        OffsetDateTime.parse(
                                "2026-11-10T08:00:00+13:00"
                        ),
                        OffsetDateTime.parse(
                                "2026-11-10T10:10:00+11:00"
                        )
                );

        Flight flightTwo =
                new Flight(
                        "NZ102",
                        destinationAirport,
                        originAirport,
                        OffsetDateTime.parse(
                                "2026-11-11T08:00:00+11:00"
                        ),
                        OffsetDateTime.parse(
                                "2026-11-11T13:00:00+13:00"
                        )
                );

        when(
                flightRepository.findAll()
        ).thenReturn(
                List.of(
                        flightOne,
                        flightTwo
                )
        );


        // Act
        List<FlightResponse> responses =
                assertDoesNotThrow(
                        () -> flightService.getFlights()
                );


        // Assert
        assertEquals(
                2,
                responses.size()
        );

        assertEquals(
                "NZ101",
                responses.get(0).flightNumber()
        );

        assertEquals(
                "NZ102",
                responses.get(1).flightNumber()
        );

        verify(
                flightRepository
        ).findAll();
    }


    /*
     * ---------------------------------------------------
     * Test data helpers
     * ---------------------------------------------------
     */

    private CreateFlightRequest createValidFlightRequest() {

        return new CreateFlightRequest(
                "NZ101",
                1L,
                2L,
                OffsetDateTime.parse(
                        "2026-11-10T08:00:00+13:00"
                ),
                OffsetDateTime.parse(
                        "2026-11-10T10:10:00+11:00"
                )
        );
    }


    private Airport createAucklandAirport() {

        return new Airport(
                "AKL",
                "NZAA",
                "Auckland Airport",
                "Auckland",
                "NZ",
                "Pacific/Auckland"
        );
    }


    private Airport createSydneyAirport() {

        return new Airport(
                "SYD",
                "YSSY",
                "Sydney Airport",
                "Sydney",
                "AU",
                "Australia/Sydney"
        );
    }
}
