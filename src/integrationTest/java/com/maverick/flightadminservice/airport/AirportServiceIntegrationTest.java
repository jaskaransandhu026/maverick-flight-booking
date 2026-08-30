package com.maverick.flightadminservice.airport;

import com.maverick.flightadminservice.TestContainersConfiguration;
import com.maverick.flightadminservice.airport.dtos.CreateAirportRequest;
import com.maverick.flightadminservice.airport.dtos.AirportResponse;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import com.maverick.flightadminservice.airport.service.AirportService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainersConfiguration.class)
@Transactional
@TestConstructor(
        autowireMode = TestConstructor.AutowireMode.ALL
)
class AirportServiceIntegrationTest {

    private final AirportService airportService;
    private final AirportRepository airportRepository;

    AirportServiceIntegrationTest(
            AirportService airportService,
            AirportRepository airportRepository
    ) {
        this.airportService = airportService;
        this.airportRepository = airportRepository;
    }


    @Test
    void createAirport_shouldPersistAirportInSqlServer() {

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


        // Act
        AirportResponse response =
                assertDoesNotThrow(
                        () -> airportService.createAirport(request)
                );


        // Assert
        assertNotNull(response);

        assertNotNull(
                response.id()
        );

        assertEquals(
                "AKL",
                response.iataCode()
        );

        assertTrue(
                airportRepository.existsByIataCode("AKL")
        );

        assertTrue(
                airportRepository.findById(response.id())
                        .isPresent()
        );
    }
}
