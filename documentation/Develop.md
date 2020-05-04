```thegardener
{
  "page" :
     {
        "label": "Develop",
        "description": "Develop on theGardener"
     }
}
```



### Architecture

![](../assets/images/theGardener_architecture.png)

### Entities

![](../assets/images/theGardener_entities.png)



### Requirements 

You will need following tools to develop on theGardener project:

| Requirement       |     Version     |      Purpose     | 
| :------------     | :-------------: | :------------ |
| git               |     >= 2.20.0   | get the sources |
| java              |     >= 1.8.0    | run sbt and scala as theGardener run over Play/Scala  |
| sbt               |     > 1.3       | get Scala dependencies and run the Play server  |
| npm               |      >= 6.5.0   | get Angular dependencies  |
| Angular CLI (ng)  |      >= 7.3.8   | angular command line to serve the front end  |
| MySQL             |     ~ 8.0.?     | store and serve data |

Note that you can absolutely use a MySQL instance running inside Docker if you don't want to setup a MySQL instance on
your machine.

### Install a dev environment

#### Sources

Get sources:
```
git clone git@github.com:KelkooGroup/theGardener.git theGardener
```

#### Data

Create an empty directory _theGardener_git_data_ to store projects sources.

#### Init dababase

Create a database on mysql called _thegardener_.

#### Application.conf

Create a file _local-config/application.local.conf_ in theGardener sources to be able to store data in the local
directory and in the local database:
```
include "application.conf"

db.default.driver=com.mysql.cj.jdbc.Driver
db.default.url="jdbc:mysql://localhost:3306/thegardener?autoReconnect=true&useSSL=false&characterEncoding=utf8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
db.default.username=root
db.default.password="somePassword"

projects.root.directory = "theGardener_git_data/"
projects.synchronize.interval = 86400
projects.synchronize.initial.delay = 10
```

#### Start the backend to apply database changes

Run the backend
```
sbt "~run -Dconfig.file=local-conf/application.local.conf"
```

Access to the Swagger doc : 
```
http://localhost:9000/api/docs
```

This will create the tables in the database
```
mysql> use thegardener ;
Database changed
mysql> show tables ;
+-----------------------+
| Tables_in_thegardener |
+-----------------------+
| branch                |
| feature               |
| feature_tag           |
| hierarchyNode         |
| play_evolutions       |
| project               |
| project_hierarchyNode |
| scenario              |
| scenario_tag          |
| tag                   |
+-----------------------+
10 rows in set (0.00 sec)
```

Stop the backend by killing the _sbt_ process.

#### Start 

Start the backend:
```
sbt "~run -Dconfig.file=local-conf/application.local.conf"
```

Start the frontend:
```
cd frontend
ng serve
```

##### Use the application 

Open [http://localhost:4200](http://localhost:4200)

##### Use the backend

Open [http://localhost:9000/api/docs](http://localhost:9000/api/docs)

### Development on Back

Know Play and Scala.

Before push:

```
sbt test
sbt scapegoat
```

### Development on Front

Know Angular.

The front is under _frontend_ directory.

Get Angular dependencies: 
```
cd frontend
npm install
```

Before push:

```
ng test
ng lint --fix
```

