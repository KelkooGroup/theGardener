```thegardener
{
  "page" :
     {
        "label": "Build a docker image",
        "description": "Build a docker image"
     }
}
```

As a theGardener developer you can build and push a Docker image of theGardener with following commands.

#### Prerequisite

_Please note that we aim to smooth the build experience with a single command in the future._

As the frontend Angular build is not included in the sbt lifecycle for now, you have to run following
commands before building a Docker image:

```
cd frontend
npm install
npm run build-prod
rm -rf ../public/dist
cp -r dist ../public/dist
cd ..
```

This will build the Angular app and copy generated files in the `public` folder included in the 
backend build.

Then, run:

```
sbt clean stage
```

#### Build an image

```
sbt docker
```

This will build an image called `kelkoogroup/thegardener` with 2 tags: `latest` and
`X.Y.Z` where `X.Y.Z` is the version defined in the `version.sbt` file.

To build the image, we use the [sbt-docker](https://github.com/marcuslonnberg/sbt-docker)
plugin which allows the Docker build to be part of the sbt lifecycle. The Dockerfile is
defined in the `build.sbt` file within the following section:
```
dockerfile in docker := {
    new Dockerfile {
        ...
    }
}
```

#### Push an image

To be able to push, you need a Docker account allowed on the _kelkoogroup_ Docker Hub
organization and you need to login first with following command:

```
docker login
```

Then you can use one of those commands:

```
sbt dockerPush # If image already built
sbt dockerBuildAndPush # To build and push in one command
```

Note that pushing an image to Docker Hub can take few minutes.


