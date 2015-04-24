-- CREATE DATABASE IF NOT EXISTS connect4_development;

-- USE connect4_development;

DROP TABLE IF EXISTS users;

CREATE TABLE users(
    id INT(11) NOT NULL AUTO_INCREMENT,
    email VARCHAR(60) UNIQUE,
    first_name VARCHAR(56),
    last_name VARCHAR(56),
  CONSTRAINT users_pk PRIMARY KEY (id)
);

DROP TABLE IF EXISTS ranks;

CREATE TABLE ranks(
    id INT(11) NOT NULL AUTO_INCREMENT,
    number INT,
    user_id INT,
  CONSTRAINT ranks_pk PRIMARY KEY (id)
);
