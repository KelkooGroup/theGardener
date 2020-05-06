```thegardener
{
  "page" :
     {
        "label": "Install theGardener",
        "description": "Install theGardener"
     }
}
```

## With Docker

You can easily run an instance of theGardener using public Docker images published 
on the [Docker Hub](https://hub.docker.com/r/kelkoogroup/thegardener/tags).

The following steps will propose you to create a `thegardener` docker container with the last version available.

It will: 
- create a simple theGardener instance with one hierarchy node and one project: theGardener documentation itself. The database will be either H2 in memory database or a mysql to persist the data from a run to another.
- download the git repositories of the registered projects in a sub directory `git-data` of the current one. 

#### New to Docker?

If you are new to Docker, here is some commands that you might need:
- stop the container: `docker container stop thegardener`
- remove the container: `docker container rm thegardener`
- view logs: `docker container logs -f thegardener`

### Usage with a H2 database

Configure and run a theGardener instance with an embedded database (H2):

```shell script
wget https://raw.githubusercontent.com/KelkooGroup/theGardener/master/docker/install.sh
chmod +x install.sh
./install.sh
```

Then go to http://localhost:9000 in your browser to navigate to the documentation.
You can now configure your application through [Configure endpoints](http://localhost:9000/app/documentation/navigate/_tools/theGardener/_/_Admin/Configure) available in the documentation itself.


### Usage with a MySQL database

The previous installation give the opportunity to play with an instance. The problem is that the database is in memory, so we need to store the data on the disk.  

You can run theGardener with a MySQL database :

Add the following lines in the `custom-application.conf` generated above.
```
db.default.driver=com.mysql.cj.jdbc.Driver
db.default.url="jdbc:mysql://mysql:3306/thegardener?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
db.default.username=root
db.default.password="root"
```

Then, you can use the following `docker-compose.yml` file:
```yaml
version: '3'
services:
  thegardener:
    image: kelkoogroup/thegardener:latest
    ports:
    - "9000:9000"
    volumes:
    - /tmp/application-mysql.conf:/app-conf/application-mysql.conf:ro
    links:
    - mysql
    entrypoint:
    - /app/bin/the_gardener
    - -Dconfig.file=/app-conf/application-mysql.conf
  mysql:
    image: mysql:8
    environment:
    - MYSQL_DATABASE=thegardener
    - MYSQL_ROOT_PASSWORD=root
```
