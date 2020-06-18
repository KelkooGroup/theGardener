# --- !Downs

ALTER TABLE hierarchyNode
    DROP COLUMN shortcut;

# --- !Ups

ALTER TABLE hierarchyNode
    ADD shortcut varchar(255);

