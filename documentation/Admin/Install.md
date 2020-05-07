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

#### New to Docker?

If you are new to Docker, here is some commands that you might need:
- stop the container: `docker container stop thegardener`
- remove the container: `docker container rm thegardener`
- view logs: `docker container logs -f thegardener`

### Install with a H2 database

The script will create a simple theGardener instance with an embedded database (H2) with one hierarchy node and one project: theGardener documentation itself.

```shell script
wget https://raw.githubusercontent.com/KelkooGroup/theGardener/master/docker/install.sh
chmod +x install.sh
./install.sh
```

Then go to http://localhost:9000 in your browser to navigate to the documentation.
You can now configure your application through [Configure endpoints](http://localhost:9000/app/documentation/navigate/_tools/theGardener/_/_Admin/Configure) available in the documentation itself.

From `/current_dir/install.sh` after the installation, you will have:
- `/current_dir/custom-application.conf`: application configuration customized based on your answers
- `/current_dir/git-data`: directory that store the git repositories sources

The script has launched the `thegardener` container the following way:

```shell script
docker run --name thegardener -p 9000:9000 \
       -v /current_dir/custom-application.conf:/app-conf/application-custom.conf:ro \
       -v /current_dir/git-data:/git-data:rw  kelkoogroup/thegardener:latest \
       -Dconfig.file=/app-conf/application-custom.conf  
```


### Install with a MySQL database

The previous installation give the opportunity to play with an instance. 
H2 is very easy to define but do not work properly with a large scale. 
You can run theGardener with a proper SQL engine, like for instance MySQL :

Add the following lines in the `/current_dir/custom-application.conf` generated above.
```
db.default.driver=com.mysql.cj.jdbc.Driver
db.default.url="jdbc:mysql://mysql:3306/thegardener?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
db.default.username=root
db.default.password="root"
```

Then, you can use the following `docker-compose.yml` file to launch the composed container:
```yaml
version: '3'
services:
  thegardener:
    image: kelkoogroup/thegardener:latest
    ports:
    - "9000:9000"
    volumes:
    - /current_dir/custom-application.conf:/app-conf/application-mysql.conf:ro
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
