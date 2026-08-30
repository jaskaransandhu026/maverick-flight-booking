package com.maverick.flightadminservice.commons.webservicelog.repository;


import com.maverick.flightadminservice.commons.webservicelog.entities.WebServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebServiceLogRepository
        extends JpaRepository<WebServiceLog, Long> {
}
