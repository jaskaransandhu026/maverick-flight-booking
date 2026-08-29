package com.maverick.flightadminservice.flight.repository;

import com.maverick.flightadminservice.flight.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    boolean existsByFlightNumberAndScheduledDeparture(
            String flightNumber,
            java.time.OffsetDateTime scheduledDeparture
    );
}
