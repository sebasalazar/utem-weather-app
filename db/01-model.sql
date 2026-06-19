BEGIN TRANSACTION;

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

COMMIT;
