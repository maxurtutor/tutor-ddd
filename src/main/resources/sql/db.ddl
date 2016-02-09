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



