ALTER TABLE member
    MODIFY COLUMN salt VARCHAR (255) NULL;

ALTER TABLE member_profile
    MODIFY COLUMN email VARCHAR (100) NOT NULL;
