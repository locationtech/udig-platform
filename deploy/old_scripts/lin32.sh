#!/bin/bash
echo "Release $0"

# ignore mac resources when using tar,zip,etc...
#!/bin/bash
echo "Release Linux 32"
source ./versions.sh

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

        echo "Extracting ${TARGET}/udig-${VERSION}.linux.gtk.x86.zip"
        unzip -q -d ${BUILD}/linux32 ${TARGET}/udig-${VERSION}.linux.gtk.x86.zip

        echo "Prepairing ${BUILD}/linux32"
        cp udig.sh ${BUILD}/linux32/udig
        cp ${BASE}/udig-1.2.x.html ${BUILD}/linux32/udig/udig-${VERSION}.html

        echo "Extracting ${JRE}/${JRE_LIN32}.tar.gz"
        
        if [ -f ${JRE}/${JRE_LIN32}.tar.gz ]
        then
            tar xzf ${JRE}/${JRE_LIN32}.tar.gz -C ${BUILD}/linux32/udig
        fi
        
        echo "Assemble ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip"
        cd ${BUILD}/linux32
        zip -9 -r -q ../udig-${VERSION}.linux.gtk.x86.zip udig
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.linux.gtk.x86.zip"
     fi
fi
