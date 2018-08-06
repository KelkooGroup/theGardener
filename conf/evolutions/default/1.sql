set ignorecase true;

# --- !Downs
DROP TABLE scenario_tag;
DROP TABLE scenario;
DROP TABLE comments;
DROP TABLE tag_feature;
DROP TABLE tag;
DROP TABLE feature;
DROP TABLE branch;
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

create table branch(
  id int not null,
  name varchar(255) not null,
  isStable BOOLEAN not null,
  projectId varchar(255) not null,
  constraint pk_branch primary key (id)
);

create table feature(
  id varchar(255) not null,
  path varchar(255) not null,
  name varchar(255) not null,
  description varchar(255) not null,
  backgroundAsJson text,
  branchId varchar(255) not null,
  language varchar (255),
  keyword varchar(255),
  comments varchar(255) not null,
  constraint pk_feature primary key(id)
);

create table tag(
    name varchar(255) not null,
    constraint pk_tag primary key(name)
);

create table feature_tag(
   featureId varchar(255) not null,
   name varchar(255) not null,
   constraint pk_tag_feature primary key (featureId, name)
);

create table comments(
    comments varchar(255) not null,
    featureId int not null
);

create table scenario(
  id int not null,
  description varchar(255) not null,
  workflowStep varchar(255) not null,
  caseType varchar(255) not null,
  abstractionLevel varchar(255) not null,
  stepsAsJson text,
  keyword varchar(255) not null,
  name varchar(255) not null,
  featureId varchar(255) not null,
  constraint pk_scenario primary key (id)
);

create table scenario_tag(
    scenarioId int not null,
    name varchar(255) not null,
    constraint pk_scenario_tag primary key (scenarioId, name)
);