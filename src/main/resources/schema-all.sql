DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);
CREATE TABLE post  (
    post_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    completed VARCHAR(255)
);