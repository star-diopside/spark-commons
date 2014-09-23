CREATE TABLE sessions
(
	id varchar PRIMARY KEY,
	data blob NOT NULL,
	modified_time bigint NOT NULL,
	last_accessed_time bigint NOT NULL,
	max_inactive_interval int NOT NULL
);
