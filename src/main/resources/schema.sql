CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL
        CONSTRAINT items_pk
            PRIMARY KEY,
    name        VARCHAR                                 NOT NULL,
    description VARCHAR                                 NOT NULL,
    owner_id    INTEGER                                 NOT NULL
        CONSTRAINT items_users_id_fk
            REFERENCES users,
    available   BOOLEAN
);



CREATE TABLE IF NOT EXISTS bookings
(
    id            INTEGER GENERATED BY DEFAULT AS IDENTITY
        CONSTRAINT bookings_pk
            PRIMARY KEY,
    start_booking TIMESTAMP NOT NULL,
    end_booking   TIMESTAMP NOT NULL,
    booker_id     INTEGER   NOT NULL
        CONSTRAINT bookings_users_id_fk
            REFERENCES users,
    item_id       INTEGER   NOT NULL
        CONSTRAINT bookings_items_id_fk
            REFERENCES items,
    status        VARCHAR   NOT NULL
);