CREATE TABLE t_user (
  user_id       CHAR(36),
  email         VARCHAR(25)  NOT NULL,
  password      VARCHAR(25),
  CONSTRAINT unique_user_id PRIMARY KEY (user_id),
);


