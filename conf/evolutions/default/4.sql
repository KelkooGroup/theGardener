# --- !Downs

ALTER TABLE project
    DROP COLUMN sourceUrlTemplate;

# --- !Ups

ALTER TABLE project
    ADD sourceUrlTemplate varchar(255);

