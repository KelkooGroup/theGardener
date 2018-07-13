set ignorecase true;

# --- !Downs
DROP TABLE project_hierarchyNode;
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
  constraint pk_hierarchyNode primary key(id)
 );

create table project_hierarchyNode(
    projectId varchar(255) not null,
    hierarchyId varchar (255) not null,
    constraint pk_project_hierarchyNode primary key (projectId, hierarchyId)
);