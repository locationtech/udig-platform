#!/bin/bash

FILES=`ls`

for file in $FILES
do
    LOGFILE=`ls $file/*.log`
    for log in $LOGFILE
        do
            cat $log|grep ERROR
        done
done

