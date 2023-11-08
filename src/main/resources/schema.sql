CREATE TABLE roles (
    id IDENTITY,
    name VARCHAR NOT NULL,
    use BOOLEAN NOT NULL
);

CREATE TABLE userinfo (
    id IDENTITY,
    username VARCHAR NOT NULL,
    role VARCHAR
);