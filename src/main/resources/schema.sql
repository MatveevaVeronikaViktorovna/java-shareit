CREATE TABLE IF NOT EXISTS users ( 
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar(128) NOT NULL,
	email varchar(128) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	name varchar(128) NOT NULL,
	description varchar(1024) NOT NULL,
	is_available boolean NOT NULL,
	owner_id BIGINT REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	start_date TIMESTAMP,
	end_date TIMESTAMP,
	item_id BIGINT REFERENCES items (id),
	booker_id BIGINT REFERENCES users (id),
	status varchar(8)
);

CREATE TABLE IF NOT EXISTS comments (
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
	text varchar(1024),
	author_id BIGINT REFERENCES users (id),
	item_id BIGINT REFERENCES items (id),
	created TIMESTAMP
)
