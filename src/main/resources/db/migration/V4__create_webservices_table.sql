CREATE TABLE webservices
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    correlation_id VARCHAR(100) NOT NULL,

    http_method VARCHAR(10) NOT NULL,

    request_path NVARCHAR(1000) NOT NULL,

    query_string NVARCHAR(2000),

    response_status INT NOT NULL,

    started_at DATETIMEOFFSET NOT NULL,

    completed_at DATETIMEOFFSET NOT NULL,

    duration_ms BIGINT NOT NULL,

    client_ip VARCHAR(100),

    user_agent NVARCHAR(1000)
);

CREATE INDEX idx_webservices_correlation_id
    ON webservices(correlation_id);

CREATE INDEX idx_webservices_started_at
    ON webservices(started_at);

CREATE INDEX idx_webservices_response_status
    ON webservices(response_status);