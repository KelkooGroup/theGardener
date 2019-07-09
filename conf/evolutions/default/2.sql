set ignorecase true;

# --- !Downs
DROP TABLE directory;
DROP TABLE page;

# --- !Ups
CREATE TABLE directory
(
    directoryId  BIGINT  NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255),
    label        VARCHAR(255),
    description  VARCHAR(255),
    `order`      INTEGER NOT NULL,
    relativePath VARCHAR(255),
    path         VARCHAR(255),
    branchId     BIGINT  NOT NULL,
    constraint pk_directory primary key (directoryId)
);

CREATE TABLE page
(
    pageId       BIGINT  NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255),
    label        VARCHAR(255),
    description  VARCHAR(255),
    `order`      INTEGER NOT NULL,
    markdown     VARCHAR(255),
    relativePath VARCHAR(255),
    path         VARCHAR(255),
    directoryId  BIGINT  NOT NULL,
    constraint pk_page primary key (pageId)
);

