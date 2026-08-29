CREATE TABLE flights
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    flight_number VARCHAR(10) NOT NULL,

    origin_airport_id BIGINT NOT NULL,
    destination_airport_id BIGINT NOT NULL,

    scheduled_departure DATETIMEOFFSET NOT NULL,
    scheduled_arrival DATETIMEOFFSET NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at DATETIMEOFFSET NOT NULL,
    updated_at DATETIMEOFFSET NOT NULL,

    CONSTRAINT fk_flights_origin_airport
        FOREIGN KEY (origin_airport_id)
            REFERENCES airports(id),

    CONSTRAINT fk_flights_destination_airport
        FOREIGN KEY (destination_airport_id)
            REFERENCES airports(id),

    CONSTRAINT chk_flights_different_airports
        CHECK (origin_airport_id <> destination_airport_id),

    CONSTRAINT chk_flights_arrival_after_departure
        CHECK (scheduled_arrival > scheduled_departure),

    CONSTRAINT uq_flight_departure
        UNIQUE (flight_number, scheduled_departure)
);

CREATE INDEX idx_flights_origin_departure
    ON flights(origin_airport_id, scheduled_departure);

CREATE INDEX idx_flights_destination_arrival
    ON flights(destination_airport_id, scheduled_arrival);