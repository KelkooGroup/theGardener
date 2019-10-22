
## Clean existing data

sudo /etc/init.d/theGardener stop

sudo rm -rf /opt/kookel/data/theGardener/git/*

launch clean_existing_data.sql

## Define settings

launch settings_database_<  >.sql

## Check result

tail -F /opt/kookel/log/theGardener/theGardener.log

sudo /etc/init.d/theGardener start

wait for "Synchronization of ${projects.size} projects is finished" in the log

check on the UI
