# --- !Ups

ALTER TABLE project
  ADD confluenceParentPageId varchar(255) null AFTER variables;

# --- !Downs

ALTER TABLE project
  DROP COLUMN confluenceParentPageId;
