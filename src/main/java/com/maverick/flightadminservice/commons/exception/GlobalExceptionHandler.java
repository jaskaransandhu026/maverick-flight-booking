package com.maverick.flightadminservice.commons.exception;

import com.maverick.flightadminservice.commons.error.service.ErrorAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorAuditService errorAuditService;

    public GlobalExceptionHandler(
            ErrorAuditService errorAuditService
    ) {
        this.errorAuditService = errorAuditService;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {

        log.warn("Bad request: {}", exception.getMessage());

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                exception.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problem);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        log.warn("Resource not found: {}", exception.getMessage());

        ProblemDetail problem = createProblem(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                exception.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problem);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {

        log.warn("Conflict: {}", exception.getMessage());

        ProblemDetail problem = createProblem(
                HttpStatus.CONFLICT,
                "Conflict",
                exception.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        Map<String, String> errors =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        fieldError -> fieldError.getField(),
                                        fieldError ->
                                                fieldError.getDefaultMessage() == null
                                                        ? "Invalid value"
                                                        : fieldError.getDefaultMessage(),
                                        (first, second) -> first,
                                        LinkedHashMap::new
                                )
                        );

        log.warn("Request validation failed fields={}", errors.keySet());

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more request fields are invalid.",
                request
        );

        problem.setProperty("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadableRequest(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {

        log.warn("Request body could not be parsed");

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid Request Body",
                "The request body is missing or contains invalid JSON.",
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {

        log.warn("Database constraint violation");

        ProblemDetail problem = createProblem(
                HttpStatus.CONFLICT,
                "Data Conflict",
                "The request conflicts with existing data.",
                request
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {

        String correlationId =
                MDC.get("correlationId");

        log.error(
                "Unexpected application error method={} path={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        String errorReference =
                errorAuditService.recordUnexpectedError(
                        exception,
                        request,
                        correlationId
                );

        ProblemDetail problem = createProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred.",
                request
        );

        problem.setProperty(
                "errorReference",
                errorReference
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem);
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request
    ) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        status,
                        detail
                );

        problem.setTitle(title);

        problem.setInstance(
                URI.create(request.getRequestURI())
        );

        problem.setProperty(
                "timestamp",
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        problem.setProperty(
                "correlationId",
                MDC.get("correlationId")
        );

        return problem;
    }
}
