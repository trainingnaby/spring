CREATE TABLE person (
    id INT PRIMARY KEY,
    name VARCHAR(50)
);

INSERT INTO person (id, name) VALUES (1, 'John');
INSERT INTO person (id, name) VALUES (2, 'Jane');
INSERT INTO person (id, name) VALUES (3, 'jean');
INSERT INTO person (id, name) VALUES (4, 'sylvain');
INSERT INTO person (id, name) VALUES (5, 'alice');
INSERT INTO person (id, name) VALUES (6, 'john');
INSERT INTO person (id, name) VALUES (7, 'serges');
INSERT INTO person (id, name) VALUES (8, 'vikash');
INSERT INTO person (id, name) VALUES (9, 'naby');
INSERT INTO person (id, name) VALUES (10, 'sylvain');
INSERT INTO person (id, name) VALUES (11, 'Tom');

CREATE TABLE person_archive (
    id INT PRIMARY KEY,
    name VARCHAR(50)
);