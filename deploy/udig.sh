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
export GTK_NATIVE_WINDOWS=1

# Add imageio-ext variables
PRGDIR=`dirname "$PRG"`
PWD=`pwd`
export GDAL_DATA="$PRGDIR/gdal_data"
echo GDAL_DATA $GDAL_DATA

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
