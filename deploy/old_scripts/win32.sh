#!/bin/bash
echo "Release"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release win32 if available
if [ -f ${TARGET}/udig-${VERSION}.win32.win32.x86.zip ] 
then
    echo "Releasing win32"
    
    if [ ! -d ${BUILD}/win32 ] 
    then
       echo "Creating ${BUILD}/win32"
       mkdir -p ${BUILD}/win32
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.win32.win32.x86.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}-win32.win32.x86.zip ..."
        echo "Extracting ${JRE}/${JRE_WIN32}.zip"
        unzip -q -d ${BUILD}/win32 ${JRE}/${JRE_WIN32}.zip
        #mv ${BUILD}/win32/${JRE_WIN32} ${BUILD}/win32/udig
        mkdir ${BUILD}/win32/udig
        mv ${BUILD}/win32/jre ${BUILD}/win32/udig/
        mv ${BUILD}/win32/gdal_data ${BUILD}/win32/udig/
        mv ${BUILD}/win32/udig/License.txt ${BUILD}/win32/udig/ImageIO-License.txt
        rm ${BUILD}/win32/udig/README.txt
        
        echo "Extracting ${TARGET}/udig-${VERSION}.win32.win32.x86.zip"
        unzip -q -d ${BUILD}/win32 ${TARGET}/udig-${VERSION}.win32.win32.x86.zip
        
        echo "Prepairing ${BUILD}/win32"
        cp ${BASE}/udig.bat ${BUILD}/win32/udig
        cp ${BASE}/udig-1.2.x.html ${BUILD}/win32/udig/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}.win32.win32.x86.zip"
        cd ${BUILD}/win32
        zip -9 -r -q ../udig-${VERSION}.win32.win32.x86.zip udig 
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.win32.win32.x86.zip"
     fi
else
  echo "Unable to find ${TARGET}/udig-${VERSION}.win32.win32.x86.zip"
fi
