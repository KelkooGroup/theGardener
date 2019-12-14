```thegardener
{
  "page" :
     {
        "label": "Install",
        "description": "How to install a new instance of theGardener ?"
     }
}
```

## With Docker

You can easily run an instance of theGardener using public Docker images published
on the [Docker Hub](https://hub.docker.com/r/kelkoogroup/thegardener).

### Basic usage

Run a theGardener instance with an embedded database (H2):

```
docker run --name thegardener -p 9000:9000 kelkoogroup/thegardener:latest
```

Then go to http://localhost:9000 in your browser.

#### New to Docker?

If you are new to Docker, here is some commands that you might need:
- stop the container: `docker container stop thegardener`
- remove the container: `docker container rm thegardener`
- view logs: `docker container logs -f thegardener`

### Load sample data

You can load sample data by using following commands.

This will configure theGardener itself as a sample project in your running instance.

```sh
curl -X POST "http://localhost:9000/api/hierarchy" \
    -H "accept: application/json" -H "Content-Type: application/json" \
    -d "{  \"id\": \".\",  \"slugName\": \"root\",  \"name\": \"root\",  \"childrenLabel\": \"Views\",  \"childLabel\": \"View\"}"

curl -X POST "http://localhost:9000/api/hierarchy" \
    -H  "accept: application/json" -H  "Content-Type: application/json" \
    -d "{  \"id\": \".01.\",  \"slugName\": \"tools\",  \"name\": \"Tools\",  \"childrenLabel\": \"Projects\",  \"childLabel\": \"Project\"}"

curl -X POST "http://localhost:9000/api/projects" \
    -H  "accept: application/json" -H  "Content-Type: application/json" \
    -d "{  \"id\": \"theGardener\",  \"name\": \"theGardener\",  \"repositoryUrl\": \"https://github.com/KelkooGroup/theGardener-template.git\",  \"stableBranch\": \"master\",  \"displayedBranches\": \"master\",  \"featuresRootPath\": \"test/features\",  \"documentationRootPath\": \"documentation\"}"

curl -X POST "http://localhost:9000/api/projects/theGardener/hierarchy/.01." \
    -H  "accept: application/json"

curl -X POST "http://localhost:9000/api/projects/theGardener/synchronize" \
    -H  "accept: application/json"
```

### Customize the configuration

If needed, you can override the application configuration by creating a custom
configuration file, let's say `/tmp/application-custom.conf` which can look like this:
```
include "application.conf"

projects.synchronize.interval = 3600
```

This will override default configuration defined in `application.conf` with your custom
values.

Then, you need to mount this file in the `/app-conf` volume in the container and add it as
an arg when running the container. For example:
```
docker run --name thegardener \
    -p 9000:9000 \
    -v /tmp/application-custom.conf:/app-conf/application-custom.conf:ro \
    kelkoogroup/thegardener:latest \
    -Dconfig.file=/app-conf/application-custom.conf
```

Puting your file in the `/app-conf` directory allows you to include the default configuration
file but you can also put it in any place you want.

### Persist Git data

If you want to persist Git data outside of the container, just mount the volume `/git-data`
in a host directory. For example:
```
docker run --name thegardener \
    -p 9000:9000 \
    -v /tmp/git-data:/git-data:rw \
    kelkoogroup/thegardener:latest
```

### Persist embedded database data

If you want to persist the embedded database data outside of the container, just mount
the volume `/data`. For example:
```
docker run --name thegardener \
    -p 9000:9000 \
    -v /tmp/db:/data:rw \
    kelkoogroup/thegardener:latest
```

You would probably want to use a real database instead of the embedded database though.
To do that, see the section above.

### Usage with a MySQL database

You can run theGardener with a MySQL database. Here is how to do with a MySQL instance
running as a container.

You will need to customize the configuration of the app (see above) with a custom
`application-mysql.conf` file configuring the database credentials.
For instance:
```
include "application.conf"

db.default.driver=com.mysql.cj.jdbc.Driver
db.default.url="jdbc:mysql://mysql:3306/thegardener?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
db.default.username=root
db.default.password="root"
```

Then, you can use the following `docker-compose.yml` file:
```
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
