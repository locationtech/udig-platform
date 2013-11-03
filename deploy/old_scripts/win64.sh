#!/bin/bash
echo "Release $0"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release macosx if available
if [ -f ${TARGET}/udig-${VERSION}.win32.win32.x86_64.zip ] 
then
    echo "Releasing win64"
    
    if [ ! -d ${BUILD}/win64/udig ] 
    then
       echo "Creating ${BUILD}/win64 directory"
       mkdir -p ${BUILD}/win64/udig
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.win32.win32.x86_64.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}-.win32.win32.x86_64.zip ..."
        echo "Extracting ${JRE}/${JRE_WIN64}.zip"
        unzip -q -d ${BUILD}/win64 ${JRE}/${JRE_WIN64}.zip
        mv ${BUILD}/win64/jre ${BUILD}/win64/udig/jre
        
        echo "Extracting ${TARGET}/udig-${VERSION}.win32.win32.x86_64.zip ..."
        unzip -q -d ${BUILD}/win64 ${TARGET}/udig-${VERSION}.win32.win32.x86_64.zip
        
        echo "Prepairing ${BUILD}/win64 .."
        mv ${BUILD}/win64/udig/udig_internal.exe ${BUILD}/win64/udig/udig.exe
        mv ${BUILD}/win64/udig/udig_internal.ini ${BUILD}/win64/udig/udig.ini
        cp ${BASE}/udig-1.2.x.html ${BUILD}/win64/udig/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}.win32.win32.x86_64.zip ..."
        cd ${BUILD}/win64
        zip -9 -r -q ../udig-${VERSION}.win32.win32.x86_64.zip udig
     else
       echo "Already Exists ${BUILD}/udig-${VERSION}.win32.win32.x86_64.zip"
     fi
fi
