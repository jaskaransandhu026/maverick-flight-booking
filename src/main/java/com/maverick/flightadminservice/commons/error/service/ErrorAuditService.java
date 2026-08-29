package com.maverick.flightadminservice.commons.error.service;

import com.maverick.flightadminservice.commons.error.entities.ApplicationError;
import com.maverick.flightadminservice.commons.error.repository.ApplicationErrorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class ErrorAuditService {

    private static final Logger log =
            LoggerFactory.getLogger(ErrorAuditService.class);

    private final ApplicationErrorRepository repository;

    public ErrorAuditService(
            ApplicationErrorRepository repository
    ) {
        this.repository = repository;
    }

    public String recordUnexpectedError(
            Exception exception,
            HttpServletRequest request,
            String correlationId
    ) {

        String errorReference =
                UUID.randomUUID().toString();

        ApplicationError error =
                new ApplicationError(
                        errorReference,
                        correlationId,
                        OffsetDateTime.now(ZoneOffset.UTC),
                        500,
                        exception.getClass().getName(),
                        truncate(exception.getMessage(), 2000),
                        request.getMethod(),
                        request.getRequestURI()
                );

        try {
            repository.saveAndFlush(error);
        } catch (Exception persistenceException) {

            // Important:
            // error auditing must never hide the original error.
            log.error(
                    "Failed to persist application error errorReference={}",
                    errorReference,
                    persistenceException
            );
        }

        return errorReference;
    }

    private String truncate(
            String value,
            int maximumLength
    ) {

        if (value == null) {
            return null;
        }

        return value.length() <= maximumLength
                ? value
                : value.substring(0, maximumLength);
    }
}