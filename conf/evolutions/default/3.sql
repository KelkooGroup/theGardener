# --- !Downs

ALTER TABLE page
  DROP COLUMN dependOnOpenApi;

# --- !Ups

ALTER TABLE page
  ADD dependOnOpenApi boolean not null  AFTER directoryId;



