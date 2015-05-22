SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS users;

CREATE TABLE users(
    id int(11) NOT NULL AUTO_INCREMENT,
    email VARCHAR(60) UNIQUE,
    first_name VARCHAR(56),
    last_name VARCHAR(56),
  CONSTRAINT users_pk PRIMARY KEY (id)
);

DROP TABLE IF EXISTS ranks;

CREATE TABLE ranks(
    id int(11) NOT NULL AUTO_INCREMENT,
    number int(11),
    user_id int(11),
  CONSTRAINT ranks_pk PRIMARY KEY (id)
);

DROP TABLE IF EXISTS games;

CREATE TABLE games(
  id int(11) NOT NULL AUTO_INCREMENT,
  player_1 int(11),
  player_2 int(11),
  height int(11),
  width int(11),
  winner int(1),
  CONSTRAINT games_pk PRIMARY KEY (id),
  CONSTRAINT games_fk1 FOREIGN KEY (player_1) REFERENCES users (id), 
  CONSTRAINT games_fk2 FOREIGN KEY (player_2) REFERENCES users (id) 
);

DROP TABLE IF EXISTS plays;

CREATE TABLE plays(
  game_id int(11),
  ord int(11),
  col int(11),
  row int(11),
  CONSTRAINT plays_pk PRIMARY KEY (game_id, ord)
);




