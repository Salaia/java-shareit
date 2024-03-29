CREATE SCHEMA IF NOT EXISTS shareit;

DROP TABLE IF EXISTS shareit.users, shareit.requests, shareit.items, shareit.comments, shareit.bookings CASCADE;

CREATE TABLE IF NOT EXISTS shareit.users (
  user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS shareit.requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(2000),
    requester_user_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(requester_user_id) REFERENCES shareit.users(user_id)
);

CREATE TABLE IF NOT EXISTS shareit.items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(2000),
    is_available BOOLEAN,
    owner_user_id BIGINT NOT NULL,
    request_id BIGINT,
    FOREIGN KEY (owner_user_id) REFERENCES shareit.users(user_id),
    FOREIGN KEY (request_id) REFERENCES shareit.requests(id)
);

CREATE TABLE IF NOT EXISTS shareit.comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(5000),
    item_item_id BIGINT NOT NULL,
    author_user_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(item_item_id) REFERENCES shareit.items(item_id),
    FOREIGN KEY(author_user_id) REFERENCES shareit.users(user_id)
);

--CREATE TYPE IF NOT EXISTS booking_status AS ENUM ('WAITING','APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS shareit.bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_item_id BIGINT,
    booker_user_id BIGINT,
    status VARCHAR,
  --  status BOOKING_STATUS,
    FOREIGN KEY(item_item_id) REFERENCES shareit.items(item_id),
    FOREIGN KEY(booker_user_id) REFERENCES shareit.users(user_id)
);