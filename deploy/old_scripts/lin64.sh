#!/bin/bash
echo "Release Linux 64"
source ./versions.sh

# Release linux 64 if available
if [ -f ${TARGET}/udig-${VERSION}.linux.gtk.x86_64.zip ] 
then
    echo "Releasing Linux 32"
    
    if [ ! -d ${BUILD}/linux64/udig ] 
    then
       echo "Creating ${BUILD}/linux64/udig"
       mkdir -p ${BUILD}/linux64/udig
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.linux.gtk.x86_64.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}.linux.gtk.x86_64.zip ..."
  
         echo "Extracting ${TARGET}/udig-${VERSION}.linux.gtk.x86_64.zip"
         unzip -q -d ${BUILD}/linux64 ${TARGET}/udig-${VERSION}.linux.gtk.x86_64.zip
     
         echo "Prepairing ${BUILD}/linux64"
         cp udig.sh ${BUILD}/linux64/udig
         cp ${BASE}/udig-1.2.x.html ${BUILD}/linux64/udig/udig-${VERSION}.html

        echo "Extracting ${JRE}/${JRE_LIN64}.tar.gz"
        
        if [ -f ${JRE}/${JRE_LIN64}.tar.gz ]
        then
            tar xzf ${JRE}/${JRE_LIN64}.tar.gz -C ${BUILD}/linux64/udig
        fi
           
        echo "Assemble ${BUILD}/udig-${VERSION}.linux.gtk.x86_64.zip"
        cd ${BUILD}/linux64
        zip -9 -r -q ../udig-${VERSION}.linux.gtk.x86_64.zip udig
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.linux.gtk.x86_64.zip"
     fi
fi
