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
  CONSTRAINT games_pk PRIMARY KEY (id),
  CONSTRAINT games_fk1 FOREIGN KEY (player_1) REFERENCES users (id), 
  CONSTRAINT games_fk2 FOREIGN KEY (player_2) REFERENCES users (id) 
);

DROP TABLE IF EXISTS grids;

CREATE TABLE grids(
	game_id int(11),
	height int(11),
	width int(11),
  CONSTRAINT grids_pk PRIMARY KEY (game_id)
);

DROP TABLE IF EXISTS cells;

CREATE TABLE cells(
	game_id int(11),
	x_coord int(11),
	y_coord int(11),
	state int(11),
  CONSTRAINT cells_pk PRIMARY KEY (game_id, x_coord, y_coord)
);




