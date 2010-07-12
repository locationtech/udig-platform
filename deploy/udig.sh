#!/bin/sh
UDIGEXEC=udig_internal

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

# Add imageio-ext variables
PRGDIR=`dirname "$PRG"`
export GDAL_DATA="$PRGDIR/gdal_data"

# Get standard environment variables
DATA_ARG=false

for ARG in $@ 
do
	if [ $ARG = "-data" ]; then DATA_ARG=true; fi
done

if $DATA_ARG; then 
	$PRGDIR/$UDIGEXEC $@
else
	$PRGDIR/$UDIGEXEC -data ~/uDigWorkspace $@
fi

