# --- !Ups

ALTER TABLE hierarchyNode
  ADD directoryPath varchar(255) NULL AFTER childLabel;

# --- !Downs

ALTER TABLE hierarchyNode
  DROP COLUMN directoryPath;

DROP INDEX globalid_idx on history;
