package com.maverick.flightadminservice.commons.error.repository;

import com.maverick.flightadminservice.commons.error.entities.ApplicationError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationErrorRepository
        extends JpaRepository<ApplicationError, Long> {
}
