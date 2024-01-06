CREATE TABLE roles (
    id IDENTITY,
    name VARCHAR NOT NULL,
    use BOOLEAN NOT NULL
);

CREATE TABLE userinfo (
    id IDENTITY,
    username VARCHAR NOT NULL,
    role VARCHAR,
    selected BOOLEAN NOT NULL
);

CREATE TABLE gamelog(
    id IDENTITY,
    name1 VARCHAR,
    role1 VARCHAR,
    name2 VARCHAR,
    role2 VARCHAR,
    name3 VARCHAR,
    role3 VARCHAR,
    name4 VARCHAR,
    role4 VARCHAR,
    result VARCHAR
);
