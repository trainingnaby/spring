INSERT INTO users (username, password, enabled) VALUES
('user', '$2a$12$TM4N0mWiFfu0Xs9Dx33pL.Yke9yH4RP5MAX4./PBP8vnLJQn0Nh.y', true),
('admin', '$2a$12$kr2w8s.lC46l00MzreA9uO1fIImEewxiBKbFwPEBvW5WX4ZP3zU66', true);

INSERT INTO authorities (username, authority) VALUES
('user', 'ROLE_USER'),
('admin', 'ROLE_ADMIN');
