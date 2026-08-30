package com.maverick.flightadminservice.commons.webservicelog.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "webservices")
public class WebServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", nullable = false, length = 100)
    private String correlationId;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "request_path", nullable = false, length = 1000)
    private String requestPath;

    @Column(name = "query_string", length = 2000)
    private String queryString;

    @Column(name = "response_status", nullable = false)
    private int responseStatus;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at", nullable = false)
    private OffsetDateTime completedAt;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "client_ip", length = 100)
    private String clientIp;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    protected WebServiceLog() {
    }

    public WebServiceLog(
            String correlationId,
            String httpMethod,
            String requestPath,
            String queryString,
            int responseStatus,
            OffsetDateTime startedAt,
            OffsetDateTime completedAt,
            long durationMs,
            String clientIp,
            String userAgent
    ) {
        this.correlationId = correlationId;
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.queryString = queryString;
        this.responseStatus = responseStatus;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMs = durationMs;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }
}
