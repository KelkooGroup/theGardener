set ignorecase true;

# --- !Downs
DROP TABLE scenario_tag;
DROP TABLE scenario;
DROP TABLE feature_tag;
DROP TABLE tag;
DROP TABLE feature;
DROP TABLE branch;
DROP TABLE project_hierarchyNode;
DROP TABLE hierarchyNode;
DROP TABLE project;

# --- !Ups
create table project
(
    id               varchar(255) not null,
    name             varchar(255) not null,
    repositoryUrl    varchar(255) not null,
    stableBranch     varchar(255) not null,
    featuresRootPath varchar(255) not null,
    constraint pk_project primary key (id)
);

create table hierarchyNode
(
    id            varchar(255) not null,
    slugName      varchar(255) not null,
    name          varchar(255) not null,
    childrenLabel varchar(255) not null,
    childLabel    varchar(255) not null,
    constraint pk_hierarchyNode primary key (id)
);

create table project_hierarchyNode
(
    projectId   varchar(255) not null,
    hierarchyId varchar(255) not null,
    constraint pk_project_hierarchyNode primary key (projectId, hierarchyId)
);

create table branch
(
    id        bigint       not null AUTO_INCREMENT,
    name      varchar(255) not null,
    isStable  BOOLEAN      not null,
    projectId varchar(255) not null,
    constraint pk_branch primary key (id)
);

create table feature
(
    id               bigint       not null AUTO_INCREMENT,
    branchId         bigint       not null,
    path             varchar(255) not null,
    backgroundAsJson text,
    language         varchar(255),
    keyword          varchar(255) not null,
    name             text         not null,
    description      text         not null,
    comments         text         not null,
    constraint pk_feature primary key (id)
);

create table tag
(
    name varchar(255) not null,
    constraint pk_tag primary key (name)
);

create table feature_tag
(
    featureId bigint       not null,
    name      varchar(255) not null,
    constraint pk_tag_feature primary key (featureId, name)
);

create table scenario
(
    id               bigint       not null AUTO_INCREMENT,
    abstractionLevel varchar(255) not null,
    caseType         varchar(255) not null,
    workflowStep     varchar(255) not null,
    keyword          varchar(255) not null,
    name             text         not null,
    description      text         not null,
    stepsAsJson      text,
    examplesAsJson   text,
    featureId        bigint       not null,
    constraint pk_scenario primary key (id)
);

create table scenario_tag
(
    scenarioId bigint       not null,
    name       varchar(255) not null,
    constraint pk_scenario_tag primary key (scenarioId, name)
);

