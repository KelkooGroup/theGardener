#!/bin/bash

currentDir=$(pwd)
applicationFile="$currentDir/custom-application.conf"
gitDataDir="$currentDir/git-data"
logFile="$currentDir/theGardener.log"


clear

echo
echo "theGardener installation"
echo
echo "This script will:"
echo " - create the directory for the data downloaded by theGardener from git repositories: $gitDataDir"
echo " - ask a few questions to customize your instance and create $applicationFile"
echo " - launch theGardener with docker with a database in memory"
echo
echo "This script assume that "
echo " - docker is installed"
echo " - no container thegardener exists "
echo "      if a previous run is still running :"
echo "           - To stop the docker container => docker stop thegardener"
echo "           - To remove the docker container => docker rm thegardener"

echo
echo
read -p "Shall we continue? [enter to continue]"
echo


echo "Several questions to customize your theGardener instance: "
echo "(Default values are working fine)"

defaultTitle='In our documentation we trust.'
read -p "   - Your instance of theGardener aim [$defaultTitle]: " title
title=${title:-$defaultTitle}

defaultLogo='assets/images/logo-white.png'
read -p "   - Url of your logo (white logo on transparent background) [$defaultLogo]: " logo
logo=${logo:-$defaultLogo}



defaultColorMain="#1F7079"
read -p "   - Main color for the header [$defaultColorMain]: " colorMain
colorMain=${colorMain:-$defaultColorMain}

defaultColorDark="#154c52"
read -p "   - Dark color for titles [$defaultColorDark]: " colorDark
colorDark=${colorDark:-$defaultColorDark}

defaultColorLight="#e7f5f7"
read -p "   - Light color for left menu [$defaultColorLight]: " colorLight
colorLight=${colorLight:-$defaultColorLight}

echo 'include "application.conf"' > $applicationFile

echo "application.windowTitle = \"$title\""  >> $applicationFile
echo "application.title =\"$title\"" >> $applicationFile
echo "application.logoSrc =\"$logo\"" >> $applicationFile
echo "application.faviconSrc =\"assets/images/favicon.png\"" >> $applicationFile
echo "color.main=\"$colorMain\"" >> $applicationFile
echo "color.dark=\"$colorDark\"" >> $applicationFile
echo "color.light=\"$colorLight\"" >> $applicationFile
echo "projects.root.directory=\"/git-data/\"" >> $applicationFile
echo
echo "The following $applicationFile has been generated:"
cat $applicationFile

FILE=/etc/resolv.conf
if [ -f "$gitDataDir" ]; then
    echo "$gitDataDir is is already there..."
else
    mkdir $gitDataDir
    echo "$gitDataDir ready to get data"
fi


echo
echo -n "Launching in background theGardener instance"

docker run --name thegardener -p 9000:9000 \
       -v $applicationFile:/app-conf/application-custom.conf:ro \
       -v $gitDataDir:/git-data:rw  kelkoogroup/thegardener:latest \
       -Dconfig.file=/app-conf/application-custom.conf  > $logFile 2> $logFile &


i="0"
while [ $i -lt 5 ]
do
  sleep 2
  echo -n "."
  ((i++))
done
echo
read -p "Open a browser on http://localhost:9000/ => an empty instance is running [enter to continue]"

read -p "Now we will configure the instance with theGardener documentation project itself [enter to continue]"

echo "Create one node called Tools:"

curl -X POST "http://localhost:9000/api/hierarchy" \
    -H "accept: application/json" -H "Content-Type: application/json" \
    -d "{  \"id\": \".\",  \"slugName\": \"root\",  \"name\": \"root\",  \"childrenLabel\": \"Views\",  \"childLabel\": \"View\"}"

curl -X POST "http://localhost:9000/api/hierarchy" \
    -H  "accept: application/json" -H  "Content-Type: application/json" \
    -d "{  \"id\": \".01.\",  \"slugName\": \"tools\",  \"name\": \"Tools\",  \"childrenLabel\": \"Projects\",  \"childLabel\": \"Project\"}"

echo ""
echo ""
echo "Register theGardener:"

curl -X POST "http://localhost:9000/api/projects" \
    -H  "accept: application/json" -H  "Content-Type: application/json" \
    -d "{  \"id\": \"theGardener\",  \"name\": \"theGardener\",  \"repositoryUrl\": \"https://github.com/KelkooGroup/theGardener.git\",  \"stableBranch\": \"master\",  \"displayedBranches\": \"master\",  \"featuresRootPath\": \"test/features\",  \"documentationRootPath\": \"documentation\", \"variables\": [{ \"name\": \"\${openApi.json.url}\", \"value\": \"http://localhost:9000/api/docs/swagger.json\"}]}"


echo ""
echo ""
echo "Attach it to the node Tools:"

curl -X POST "http://localhost:9000/api/projects/theGardener/hierarchy/.01." \
    -H  "accept: application/json"

curl -X POST "http://localhost:9000/api/projects/theGardener/synchronize" \
    -H  "accept: application/json"


echo ""
echo ""
echo -n "Downloading theGardener project, please wait"

until [ -f git-data/theGardener/master/documentation/thegardener.json ]
do
     sleep 2
     echo -n "."
done
sleep 2
echo ""

echo ""
echo ""
echo "Refresh the menu"
curl -X POST "http://localhost:9000/api/admin/menu/refreshFromDatabase" -H  "accept: application/json"

echo ""
sleep 2
echo ""
echo "Refresh your browser on  http://localhost:9000, you will have theGardener documentation register on your instance."

echo ""
echo ""
echo "On http://localhost:9000/app/documentation/navigate/_tools/theGardener/_/_Admin/Configure, you will be able to configure your instance with your hierachy and projects."



echo " - To stop the docker container => docker stop thegardener"
echo " - To remove the docker container => docker rm thegardener"




