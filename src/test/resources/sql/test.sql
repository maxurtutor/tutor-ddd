DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_user_role;

CREATE TABLE t_user (
  user_id       CHAR(36),
  email         VARCHAR(25)  NOT NULL,
  password      VARCHAR(25),
  CONSTRAINT unique_user_id PRIMARY KEY (user_id)
);

CREATE TABLE t_role (
  role_id       CHAR(36),
  CONSTRAINT unique_role_id PRIMARY KEY (role_id)
);

CREATE TABLE t_user_role (
  user_id       CHAR(36),
  role_id       CHAR(36),
  CONSTRAINT unique_user_id_role_id PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id)
  REFERENCES t_user (user_id),
  FOREIGN KEY (role_id)
  REFERENCES t_role (role_id)
);

INSERT INTO t_user (user_id, email, password)
VALUES ('u1', 'ivan@mail.com', 'p1');
INSERT INTO t_user (user_id, email, password)
VALUES ('u2', 'petr@mail.com', 'p2');
INSERT INTO t_user (user_id, email, password)
VALUES ('u3', 'sidor@mail.com', 'p3');

INSERT INTO t_role (role_id)
VALUES ('ADMIN');
INSERT INTO t_role (role_id)
VALUES ('USER');

INSERT INTO t_user_role (user_id, role_id)
VALUES ('u1', 'ADMIN');
INSERT INTO t_user_role (user_id, role_id)
VALUES ('u1', 'USER');
INSERT INTO t_user_role (user_id, role_id)
VALUES ('u2', 'USER');
