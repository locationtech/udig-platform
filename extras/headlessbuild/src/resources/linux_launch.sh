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

# Get standard environment variables
PRGDIR=`dirname "$PRG"`
DATA_ARG=false

for ARG in $@ 
do
	if [ $ARG = "-data" ]; then DATA_ARG=true; fi
done

if $DATA_ARG; then 
	$PRGDIR/bin $@
else
	$PRGDIR/bin -data ~/uDigWorkspace $@
fi
