# --- !Ups
ALTER TABLE MEMBER ADD COLUMN RENEWAL TINYINT(1) DEFAULT 1 AFTER FEE;
ALTER TABLE MEMBER ADD COLUMN END DATE NOT NULL AFTER SINCE;
ALTER TABLE MEMBER ADD COLUMN ACTIVE TINYINT(1) DEFAULT 0 AFTER END;
ALTER TABLE PERSON ADD COLUMN CUSTOMER_ID VARCHAR(254) AFTER BLOG;
ALTER TABLE ORGANISATION ADD COLUMN CUSTOMER_ID VARCHAR(254) AFTER BLOG;

UPDATE MEMBER SET END = CURDATE();

create table if not exists PAYMENT_RECORD (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  REMOTE_ID VARCHAR(254) NOT NULL UNIQUE KEY,
  PAYER_ID BIGINT NOT NULL,
  OBJECT_ID BIGINT NOT NULL,
  PERSON TINYINT(1) NOT NULL,
  DESCRIPTION VARCHAR(254) NOT NULL,
  FEE_CURRENCY CHAR(3) NOT NULL,
  FEE DECIMAL(13,3) NOT NULL,
  CREATED DATETIME NOT NULL);

# --- !Downs
ALTER TABLE MEMBER DROP COLUMN RENEWAL;
ALTER TABLE MEMBER DROP COLUMN END;
ALTER TABLE MEMBER DROP COLUMN ACTIVE;
ALTER TABLE PERSON DROP COLUMN CUSTOMER_ID;
ALTER TABLE ORGANISATION DROP COLUMN CUSTOMER_ID;

drop table PAYMENT_RECORD;