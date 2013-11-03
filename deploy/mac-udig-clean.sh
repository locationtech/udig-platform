#!/bin/sh

PRG="$0"

while [ -h "$PRG" ]; do
	ls=`ls -ld "$PRG"`
	link=`expr "$ls" : '.*-> \(.*\)$'`
	if expr "$link" : '/.*' > /dev/null; then
		PRG="$link"
	else
		PRG=`dirname "$PRG"`/"$link"
	fi
done

PRG=`dirname "$PRG"`
$PRG/udig.app/Contents/MacOS/udig_internal -clean