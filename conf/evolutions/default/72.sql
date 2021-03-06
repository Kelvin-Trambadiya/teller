# --- !Ups
create table PERMANENT_SESSION(
  ID VARCHAR(512) NOT NULL,
  USER_ID VARCHAR(512) NOT NULL,
  PROVIDER_ID VARCHAR(512) NOT NULL,
  CREATION_DATE TIMESTAMP NOT NULL,
  LAST_USED TIMESTAMP NOT NULL,
  EXPIRATION_DATE TIMESTAMP NOT NULL
);

# --- !Downs
drop table PERMANENT_SESSION;