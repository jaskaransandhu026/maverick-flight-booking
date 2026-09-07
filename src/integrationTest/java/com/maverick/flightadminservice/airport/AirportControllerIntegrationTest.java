package com.maverick.flightadminservice.airport;

import com.maverick.flightadminservice.TestContainersConfiguration;
import com.maverick.flightadminservice.airport.dtos.AirportResponse;

import com.maverick.flightadminservice.airport.entities.Airport;
import com.maverick.flightadminservice.airport.repository.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
@Import(TestContainersConfiguration.class)
class AirportControllerIntegrationTest {

    @Autowired
    private RestTestClient restClient;

    @Autowired
    private AirportRepository airportRepository;


    /**
     * That is useful because HTTP requests run through Tomcat in a separate transaction,
     * so simply putting @Transactional on the test class does not reliably roll back what
     * the endpoint committed.
     */
    @BeforeEach
    void cleanDatabase() {
        airportRepository.deleteAll();
    }


    @Test
    void createAirport_shouldReturn201AndPersistAirport() {

        String requestBody = """
            {
                "iataCode": "AKL",
                "icaoCode": "NZAA",
                "name": "Auckland Airport",
                "city": "Auckland",
                "countryCode": "NZ",
                "timezone": "Pacific/Auckland"
            }
            """;

        AirportResponse response =
                restClient
                        .post()
                        .uri("/api/v1/admin/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectHeader()
                        .exists("X-Correlation-Id")
                        .expectBody(AirportResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response);

        assertNotNull(response.id());

        assertEquals(
                "AKL",
                response.iataCode()
        );

        assertEquals(
                "NZAA",
                response.icaoCode()
        );
    }


    @Test
    void createAirport_shouldReturn409_whenIataAlreadyExists() {

        String requestBody = """
            {
                "iataCode": "AKL",
                "icaoCode": "NZAA",
                "name": "Auckland Airport",
                "city": "Auckland",
                "countryCode": "NZ",
                "timezone": "Pacific/Auckland"
            }
            """;

        // First request succeeds
        restClient
                .post()
                .uri("/api/v1/admin/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus()
                .isCreated();


        // Same airport again
        restClient
                .post()
                .uri("/api/v1/admin/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus()
                .isEqualTo(409);
    }

    @Test
    void createAirport_shouldReturn400_whenIataCodeIsBlank() {

        String requestBody = """
            {
                "iataCode": "",
                "icaoCode": "NZAA",
                "name": "Auckland Airport",
                "city": "Auckland",
                "countryCode": "NZ",
                "timezone": "Pacific/Auckland"
            }
            """;

        restClient
                .post()
                .uri("/api/v1/admin/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }


    @Test
    void getAirport_shouldReturn200_whenAirportExists() {

        // Arrange
        Airport airport =
                new Airport(
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland"
                );

        Airport savedAirport =
                airportRepository.save(airport);


        // Act + Assert
        AirportResponse response =
                restClient
                        .get()
                        .uri("/api/v1/admin/airports/{id}", savedAirport.getId())
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AirportResponse.class)
                        .returnResult()
                        .getResponseBody();


        assertNotNull(response);

        assertEquals(
                savedAirport.getId(),
                response.id()
        );

        assertEquals(
                "AKL",
                response.iataCode()
        );

        assertEquals(
                "NZAA",
                response.icaoCode()
        );

        assertEquals(
                "Auckland Airport",
                response.name()
        );
    }

    @Test
    void getAirport_shouldReturn404_whenAirportDoesNotExist() {

        // Arrange
        Long airportId = 999999L;


        // Act + Assert
        restClient
                .get()
                .uri("/api/v1/admin/airports/{id}", airportId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getAirports_shouldReturn200AndListOfAirports() {

        // Arrange
        Airport aucklandAirport =
                new Airport(
                        "AKL",
                        "NZAA",
                        "Auckland Airport",
                        "Auckland",
                        "NZ",
                        "Pacific/Auckland"
                );

        Airport sydneyAirport =
                new Airport(
                        "SYD",
                        "YSSY",
                        "Sydney Airport",
                        "Sydney",
                        "AU",
                        "Australia/Sydney"
                );

        airportRepository.save(aucklandAirport);
        airportRepository.save(sydneyAirport);


        // Act + Assert
        List<AirportResponse> response =
                restClient
                        .get()
                        .uri("/api/v1/admin/airports")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(
                                new ParameterizedTypeReference<List<AirportResponse>>() {}
                        )
                        .returnResult()
                        .getResponseBody();


        assertNotNull(response);

        assertEquals(
                2,
                response.size()
        );

        assertTrue(
                response.stream()
                        .anyMatch(
                                airport ->
                                        airport.iataCode().equals("AKL")
                        )
        );

        assertTrue(
                response.stream()
                        .anyMatch(
                                airport ->
                                        airport.iataCode().equals("SYD")
                        )
        );
    }

}
