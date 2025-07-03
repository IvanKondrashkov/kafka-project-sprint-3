CREATE TABLE IF NOT EXISTS users(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    private_info VARCHAR
);

INSERT INTO users(id, name, private_info)
SELECT
    i,
    'Name_' || i || '_' || substring('abcdefghijklmnopqrstuvwxyz', (random() * 26)::integer + 1, 1),
    'Private_info_' || i || '_' || substring('abcdefghijklmnopqrstuvwxyz', (random() * 26)::integer + 1, 1)
FROM generate_series(1, 9000000) AS i;