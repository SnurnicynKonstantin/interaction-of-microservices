CREATE TABLE test_db.message (
	id INT auto_increment NOT NULL PRIMARY KEY,
	session_id INT NOT NULL,
	mc1_timestamp DATETIME NOT NULL,
	mc2_timestamp DATETIME,
	mc3_timestamp DATETIME,
	end_timestamp DATETIME
)
ENGINE=InnoDB
DEFAULT CHARSET=latin1
COLLATE=latin1_swedish_ci;

