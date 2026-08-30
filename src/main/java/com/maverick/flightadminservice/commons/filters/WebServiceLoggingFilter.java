package com.maverick.flightadminservice.commons.filters;

import com.maverick.flightadminservice.commons.webservicelog.entities.WebServiceLog;
import com.maverick.flightadminservice.commons.webservicelog.service.WebServiceLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class WebServiceLoggingFilter
        extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(WebServiceLoggingFilter.class);

    private final WebServiceLogService webServiceLogService;

    public WebServiceLoggingFilter(
            WebServiceLogService webServiceLogService
    ) {
        this.webServiceLogService = webServiceLogService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        OffsetDateTime startedAt =
                OffsetDateTime.now(ZoneOffset.UTC);

        try {

            filterChain.doFilter(request, response);

        } finally {

            OffsetDateTime completedAt =
                    OffsetDateTime.now(ZoneOffset.UTC);

            long durationMs =
                    Duration.between(
                            startedAt,
                            completedAt
                    ).toMillis();

            String correlationId =
                    MDC.get(CorrelationIdFilter.MDC_KEY);

            WebServiceLog webServiceLog =
                    new WebServiceLog(
                            correlationId,
                            request.getMethod(),
                            request.getRequestURI(),
                            request.getQueryString(),
                            response.getStatus(),
                            startedAt,
                            completedAt,
                            durationMs,
                            getClientIp(request),
                            request.getHeader("User-Agent")
                    );

            try {

                webServiceLogService.save(webServiceLog);

            } catch (Exception exception) {

                /*
                 * Never allow access logging failure
                 * to break the user's API request.
                 */
                log.error(
                        "Failed to persist web service log correlationId={}",
                        correlationId,
                        exception
                );
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        /*
         * Only record our actual API requests.
         *
         * This prevents Swagger, actuator, etc.
         * filling the table.
         */
        return !request.getRequestURI()
                .startsWith("/api/");
    }

    private String getClientIp(
            HttpServletRequest request
    ) {

        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }
}
