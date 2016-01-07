CREATE TABLE t_team (
  team_id      CHAR(36) CONSTRAINT unique_team_id PRIMARY KEY,
  team_name    VARCHAR(100) NOT NULL UNIQUE,
  max_capacity INT          NOT NULL,
  update_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_user (
  user_id  CHAR(36),
  team_id CHAR(36)     NOT NULL,
  user_name     VARCHAR(100) NOT NULL UNIQUE,
  first_name    VARCHAR(100) NOT NULL,
  last_name     VARCHAR(100) NOT NULL,
  email         VARCHAR(25),
  password      VARCHAR(32),
  CONSTRAINT unique_user_id PRIMARY KEY (user_id),
  FOREIGN KEY (team_id)
  REFERENCES t_team (team_id)
);


