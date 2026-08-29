package com.maverick.flightadminservice.commons.error.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "application_errors")
public class ApplicationError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "error_reference",
            nullable = false,
            unique = true,
            length = 36
    )
    private String errorReference;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "http_status", nullable = false)
    private int httpStatus;

    @Column(
            name = "exception_type",
            nullable = false,
            length = 255
    )
    private String exceptionType;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "request_path", length = 1000)
    private String requestPath;

    protected ApplicationError() {
    }

    public ApplicationError(
            String errorReference,
            String correlationId,
            OffsetDateTime occurredAt,
            int httpStatus,
            String exceptionType,
            String errorMessage,
            String httpMethod,
            String requestPath
    ) {
        this.errorReference = errorReference;
        this.correlationId = correlationId;
        this.occurredAt = occurredAt;
        this.httpStatus = httpStatus;
        this.exceptionType = exceptionType;
        this.errorMessage = errorMessage;
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
    }
}
