#!/bin/bash


set -x

if [ -z "$1" ]
then
  echo "The directory to base the search from must be entered"
  exit 0
fi

cd $1
mkdir translate

MESSAGES=`find . -name messages*.properties|grep -v .*/bin`

for file in $MESSAGES
do
    path=`dirname $file`

    mkdir -p translate/$path
    cp $file translate/$path/
done

PLUGINS=`find . -name plugin*.properties | grep -v .*/bin`

for file in $PLUGINS
do
  path=`dirname $file`

  mkdir -p translate/$path
  cp $file translate/$path/
done


zip -r translationfiles.zip translate
rm -rf translate
