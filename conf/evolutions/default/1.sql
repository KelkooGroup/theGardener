set ignorecase true;

# --- !Downs
DROP TABLE hierarchyNode;
DROP TABLE project;

# --- !Ups
create table project(
    id varchar(255) not null,
    name varchar(255) not null,
    repositoryUrl varchar(255) not null,
    stableBranch varchar(255) not null,
    featuresRootPath varchar(255) not null,
    constraint pk_project primary key (id)
);

create table hierarchyNode(
  id varchar(255) not null,
  slugName varchar(255) not null,
  name varchar (255) not null,
  constraint pk_hierarchy primary key(id)
 );