#!/bin/bash
# ignore mac resources when using tar,zip,etc...
#
export SCRIPTS=`dirname $0`

cd ${SCRIPTS}
./mac64.sh

cd ${SCRIPTS}
./win32.sh

cd ${SCRIPTS}
./win64.sh

cd ${SCRIPTS}
./lin32.sh

cd ${SCRIPTS}
./lin64.sh
