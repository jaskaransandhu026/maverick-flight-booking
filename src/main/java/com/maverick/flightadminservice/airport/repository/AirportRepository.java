package com.maverick.flightadminservice.airport.repository;

import com.maverick.flightadminservice.airport.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    boolean existsByIataCode(String iataCode);
}
