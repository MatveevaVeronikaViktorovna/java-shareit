DROP TABLE IF EXISTS items, users;

CREATE TABLE IF NOT EXISTS users ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	email varchar NOT NULL UNIQUE, 
	name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar NOT NULL,
	description varchar NOT NULL,
	available boolean NOT NULL,
	user_id BIGINT REFERENCES users (id)
);