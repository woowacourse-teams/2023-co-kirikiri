ALTER TABLE member
    MODIFY COLUMN nickname varchar(30) NOT NULL;

ALTER TABLE member
    MODIFY COLUMN password varchar(255) NULL;

ALTER TABLE member
    ADD COLUMN oauth_id varchar(255) NULL;

ALTER TABLE member_profile
    DROP COLUMN phone_number;

ALTER TABLE member_profile
    DROP COLUMN birthday;

ALTER TABLE member_profile
    ADD COLUMN email varchar(100) NULL;
