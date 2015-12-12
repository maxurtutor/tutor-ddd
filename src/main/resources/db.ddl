CREATE TABLE t_group (
  group_id CHAR(36) PRIMARY KEY,
  group_name     VARCHAR(100) NOT NULL UNIQUE,
  max_capacity   INT NOT NULL,
  update_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_user (
  user_id  CHAR(36) PRIMARY KEY,
  group_id CHAR(36)     NOT NULL,
  user_name     VARCHAR(100) NOT NULL UNIQUE,
  first_name    VARCHAR(100) NOT NULL,
  last_name     VARCHAR(100) NOT NULL,
  email         VARCHAR(25),
  FOREIGN KEY (group_id)
  REFERENCES t_group (group_id)
);


