#!/bin/sh


# first do an svn update to get the latest version of 
# the build scripts
svn update .

if [ $? != 0 ]; then
	echo "ERROR: Unable top update build scripts.";	
	exit
fi

# read repository property from build.properties
REPO_URL=`grep "^ *repo.url" build.properties`
REPO_URL=${REPO_URL:9}

# call the main build script
sh build.sh $REPO_URL

