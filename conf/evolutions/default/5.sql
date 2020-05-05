# --- !Downs

drop index i_scenario_featureId ON scenario (featureId) ;
drop index i_page_path ON page (path) ;
drop index i_directory_path ON directory (path) ;

# --- !Ups

create index i_scenario_featureId ON scenario (featureId) ;
create index i_page_path ON page (path) ;
create index i_directory_path ON directory (path) ;

