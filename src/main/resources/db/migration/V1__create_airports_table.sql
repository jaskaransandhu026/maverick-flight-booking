CREATE TABLE airports
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    iata_code VARCHAR(3) NOT NULL,
    icao_code VARCHAR(4) NOT NULL,

    name VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    timezone VARCHAR(100) NOT NULL,

    active BIT NOT NULL,

    created_at DATETIMEOFFSET NOT NULL,
    updated_at DATETIMEOFFSET NOT NULL,

    CONSTRAINT uq_airports_iata UNIQUE (iata_code),
    CONSTRAINT uq_airports_icao UNIQUE (icao_code)
);