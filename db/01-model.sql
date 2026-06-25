BEGIN TRANSACTION;

CREATE EXTENSION postgis;

DROP TABLE IF EXISTS pharmacies CASCADE;
CREATE TABLE pharmacies (
    pk          bigint           GENERATED ALWAYS AS IDENTITY,  -- Clave primaria interna
    commerce    smallint         NOT NULL,
    store_id    int              NOT NULL,
    name        varchar(255)     NOT NULL,
    address     varchar(255)     NOT NULL,
    phone       bigint           NOT NULL,
    start_time  time             NOT NULL,
    end_time    time             NOT NULL,
    latitude    double precision NOT NULL,
    longitude   double precision NOT NULL,
    created     timestamptz      NOT NULL DEFAULT NOW(),        -- Fecha de creación
    updated     timestamptz      NOT NULL DEFAULT NOW(),        -- Fecha de última modificación
    PRIMARY KEY (pk)
);
CREATE INDEX ON pharmacies(commerce);
CREATE INDEX ON pharmacies(latitude,longitude);
CREATE INDEX ON pharmacies USING gist (ST_MakePoint(longitude, latitude)::geography);
CREATE UNIQUE INDEX ON pharmacies(store_id);



DROP TABLE IF EXISTS pharmacies_on_duty CASCADE;
CREATE TABLE pharmacies_on_duty (
    pk          bigint           GENERATED ALWAYS AS IDENTITY,  -- Clave primaria interna
    pharmacy_fk bigint           NOT NULL,
    duty_date   date             NOT NULL,
    created     timestamptz      NOT NULL DEFAULT NOW(),        -- Fecha de creación
    updated     timestamptz      NOT NULL DEFAULT NOW(),        -- Fecha de última modificación
    PRIMARY KEY (pk)
);
CREATE UNIQUE INDEX ON pharmacies_on_duty(pharmacy_fk, duty_date);



DROP TABLE IF EXISTS stations CASCADE;
CREATE TABLE stations (
    pk bigserial NOT NULL,
    code varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    altitude int NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created timestamptz NOT NULL DEFAULT NOW(),
    updated timestamptz NOT NULL DEFAULT NOW(),
    PRIMARY KEY (pk)
);
CREATE UNIQUE INDEX ON stations(UPPER(code));
CREATE INDEX ON stations(latitude,longitude);
CREATE INDEX ON stations USING gist (ST_MakePoint(longitude, latitude)::geography);


DROP TABLE IF EXISTS observations CASCADE;
CREATE TABLE observations (
    pk bigserial NOT NULL,
    station_fk bigint NOT NULL,
    code varchar(255) NOT NULL,
    date_time timestamptz NOT NULL DEFAULT NOW(),
    temperature double precision NOT NULL DEFAULT -1,
    humidity double precision NOT NULL DEFAULT -1,
    wind_speed double precision NOT NULL DEFAULT -1,
    wind_direction bigint NOT NULL DEFAULT -1,
    solar_radiation double precision NOT NULL DEFAULT -1,
    absolute_pressure double precision NOT NULL DEFAULT -1,
    precipitation double precision NOT NULL DEFAULT -1,
    dew_point double precision NOT NULL DEFAULT -1,
    wind_gust double precision NOT NULL DEFAULT -1,
    pressure double precision NOT NULL DEFAULT -1,
    rain_rate double precision NOT NULL DEFAULT -1,
    ultraviolet bigint NOT NULL DEFAULT -1,
    daily_rainfall double precision NOT NULL DEFAULT -1,
    created timestamptz NOT NULL DEFAULT NOW(),
    updated timestamptz NOT NULL DEFAULT NOW(),
    FOREIGN KEY (station_fk) REFERENCES stations(pk) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (pk)
);
CREATE UNIQUE INDEX ON observations(station_fk, UPPER(code));


COMMIT;
