# --- !Ups

ALTER TABLE project
  ADD variables text AFTER documentationRootPath;

ALTER TABLE hierarchyNode
  ADD directoryPath varchar(255) NULL AFTER childLabel;

# --- !Downs

ALTER TABLE hierarchyNode
  DROP COLUMN directoryPath;

ALTER TABLE project
  DROP COLUMN variables text ;
