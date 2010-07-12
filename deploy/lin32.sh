#!/bin/bash
echo "Release Linux 32"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

JRE_LIN32

# Release win32 if available
if [ -f ${TARGET}/udig-${VERSION}.linux.gtk.x86.zip ] 
then
    echo "Releasing Linux 32"
    
    if [ ! -d ${BUILD}/linux32/udig ] 
    then
       echo "Creating ${BUILD}/win32/udig"
       mkdir -p ${BUILD}/linux32/udig
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip ..."
        echo "Extracting ${JRE_LIN32}"
        
        #unzip -xf {$JRE_LIN32}
        if [ -f ${JRE_LIN32} ]
        then
            gunzip -q -d ${BUILD}/linux32 ${JRE_LIN32}
        fi
        
        if [ -f ${JRE}/${JRE_LIN32_DIR}.tar ]
        then
            cd build/linux32/udig
            tar xf ../../../${JRE}/${JRE_LIN32_DIR}.tar
            cd ../../..
        fi
        
        echo "Extracting ${TARGET}/udig-${VERSION}.linux.gtk.x86.zip"
        unzip -q -d ${BUILD}/linux32 ${TARGET}/udig-${VERSION}.linux.gtk.x86.zip
        
        echo "Prepairing ${BUILD}/linux32"
        cp udig.sh ${BUILD}/linux32/udig
        
        echo "Assemble ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip"
        zip -9 -r -q ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip ${BUILD}/linux32/udig
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip"
     fi
fi
