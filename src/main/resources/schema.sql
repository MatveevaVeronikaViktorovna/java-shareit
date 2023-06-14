DROP TABLE IF EXISTS items, users, bookings, comments;

CREATE TABLE IF NOT EXISTS users ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar NOT NULL,
	email varchar NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar NOT NULL,
	description varchar NOT NULL,
	is_available boolean NOT NULL,
	owner_id BIGINT REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	start_date TIMESTAMP,
	end_date TIMESTAMP,
	item_id BIGINT REFERENCES items (id),
	booker_id BIGINT REFERENCES users (id),
	status varchar
);

CREATE TABLE IF NOT EXISTS comments (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	text varchar,
	author_id BIGINT REFERENCES users (id),
	item_id BIGINT REFERENCES items (id),
	created TIMESTAMP
)
