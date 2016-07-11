DROP TABLE IF EXISTS TASK;

CREATE TABLE TASK (
  ID VARCHAR(50) NOT NULL,
  SUMMARY VARCHAR(50) NOT NULL UNIQUE,
  DESCRIPTION VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (ID)
);


