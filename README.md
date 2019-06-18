![the Gardener](https://raw.githubusercontent.com/KelkooGroup/theGardener/master/public/images/logo.png) 

[![Travis](https://travis-ci.org/KelkooGroup/theGardener.svg?branch=master)](https://travis-ci.org/KelkooGroup/theGardener) [![CodeFactor](https://www.codefactor.io/repository/github/kelkoogroup/thegardener/badge)](https://www.codefactor.io/repository/github/kelkoogroup/thegardener) [![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=theGardener&metric=alert_status)](https://sonarcloud.io/dashboard?id=theGardener) [![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=theGardener&metric=bugs)](https://sonarcloud.io/dashboard?id=theGardener) [![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=theGardener&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=theGardener) [![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=theGardener&metric=code_smells)](https://sonarcloud.io/dashboard?id=theGardener)


## How to install a version to test the application ?

See https://github.com/KelkooGroup/theGardener/wiki/Installation

## How to develop locally ?

### Launch local server

Run local server with resources hot reload:
`sbt ~run`

Run local server with resources hot reload and remote debugging enabled:
`sbt -jvm-debug 9999 ~run`

Run Angular Live Development server, under `frontend` directory
`ng serve`  


##### Application play
run on 9000, for instance Swagger is available on http://localhost:9000/docs/  

##### Application angular
run on 4200, for instance the UI is available on http://localhost:4200/


Note: on production, the Angular Live Development Server is not running, all requests are done on the play application.
