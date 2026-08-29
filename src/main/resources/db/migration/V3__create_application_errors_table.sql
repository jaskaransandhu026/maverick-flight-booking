CREATE TABLE application_errors
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    error_reference VARCHAR(36) NOT NULL,
    correlation_id VARCHAR(100),

    occurred_at DATETIMEOFFSET NOT NULL,

    http_status INT NOT NULL,

    exception_type VARCHAR(255) NOT NULL,

    error_message NVARCHAR(2000),

    http_method VARCHAR(10),

    request_path NVARCHAR(1000),

    CONSTRAINT uq_application_errors_reference
        UNIQUE (error_reference)
);

CREATE INDEX idx_application_errors_correlation_id
    ON application_errors(correlation_id);

CREATE INDEX idx_application_errors_occurred_at
    ON application_errors(occurred_at);