#!/bin/bash
source ./versions.sh
function assemble() {
    PLATFORM=$1
    EXT=$2
    PLATFORM_JRE=$3
    
    echo "Looking for ${TARGET}/udig-${VERSION}.${EXT}.zip"
    
    
    # Copy to TARGET if needed
    # if [ ! -f ${TARGET}/udig-${VERSION}.${EXT}.zip ] 
    #then
    #    echo "Looking for ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip"
    #    if [ -f ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip ] 
    #    then
    #        echo "Staging ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip to target directory for release"
    #        cp ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip ${TARGET}/udig-${VERSION}.${EXT}.zip
    #    else 
    #        echo "Unable to locate a build of udig-${VERSION}.${EXT}.zip to release"
    #    fi
    #fi
    
    # Release win32 if available
    if [ -f ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip ] 
    then
        echo "Releasing ${PLATFORM}"

        if [ ! -d ${BUILD}/${PLATFORM}/udig ] 
        then
           echo "Creating ${BUILD}/${PLATFORM}/udig"
           mkdir -p ${BUILD}/${PLATFORM}/udig
        fi

        if [ ! -f ${BUILD}/udig-${VERSION}.${EXT}.zip ]
        then
            echo "Building ${BUILD}/udig-${VERSION}.${EXT}.zip ..."

            echo "Extracting ${TARGET}/udig-${VERSION}.${EXT}.zip"
            unzip -q -d ${BUILD}/${PLATFORM}/udig ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip

            echo "Preparing ${BUILD}/${PLATFORM} with ${JRE}/${PLATFORM_JRE}"

            extract_jre
            
            echo "Preparing ${BUILD}/${PLATFORM} with start up scripts and html files"
            prepare_resources

            echo "Assemble ${BUILD}/udig-${VERSION}.${EXT}.zip"
            cd ${BUILD}/${PLATFORM}
            zip -9 -r -q ../udig-${VERSION}.${EXT}.zip udig
         else 
           echo "Already Exists ${BUILD}/udig-${VERSION}.${EXT}.zip"
         fi
    fi
    
}

function prepare_resources () {

    for opt in `find ../plugins/ -name .options` ; do cat $opt >> ${BUILD}/${PLATFORM}/udig/debug-options ; done
    cp ${BASE}/udig-1.3.x.html ${BUILD}/${PLATFORM}/udig/udig-${VERSION}.html
    cat ../plugins/net.refractions.udig.libs/.options >> ${BUILD}/${PLATFORM}/udig/.options
    mkdir ${BUILD}/${PLATFORM}/udig/dropins
    
    if [[ $PLATFORM == linux* ]] ; then
        cp udig.sh ${BUILD}/${PLATFORM}/udig
        chmod 755 ${BUILD}/${PLATFORM}/udig/udig_internal
        cp udig-clean.sh ${BUILD}/${PLATFORM}/udig
        cp udig-debug.sh ${BUILD}/${PLATFORM}/udig
    fi
    if [[ $PLATFORM == win* ]] ; then
        cp *.bat ${BUILD}/${PLATFORM}/udig
        mkdir -p ${BUILD}/${PLATFORM}/udig/icons
        cp installer/*.ico ${BUILD}/${PLATFORM}/udig/icons/
        windows_installer
    fi
    if [[ $PLATFORM == mac* ]] ; then
        chmod 755 ${BUILD}/${PLATFORM}/udig/udig_internal.app/Contents/MacOS/udig_internal
        mv ${BUILD}/${PLATFORM}/udig/udig_internal.app ${BUILD}/${PLATFORM}/udig/udig.app
        rm ${BUILD}/${PLATFORM}/udig/udig_internal
        ln -s udig ${BUILD}/${PLATFORM}/udig/udig.app/Contents/MacOS/udig_internal
        cp mac-udig-clean.sh ${BUILD}/${PLATFORM}/udig/udig-clean.sh
        cp mac-udig-debug.sh ${BUILD}/${PLATFORM}/udig/udig-debug.sh
        mv ${BUILD}/${PLATFORM}/udig/.options ${BUILD}/${PLATFORM}/udig/udig.app/Contents/MacOS/
    fi
}

function windows_installer () {
    MAKENSIS=`which makensis`
    if [ $? == 0 ] ; then
        HERE=`pwd`
        # todo use sed or something to update VERSION inside uDigInstallScript.nsi
        
        cp ${INSTALLER}/32-uDigIcon.ico ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/ECWEULA.txt ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/LICENSE.txt ${BUILD}/${PLATFORM}
        sed -e "s/VersionXXXX/${VERSION}/g" ${INSTALLER}/uDigInstallScript.nsi > ${BUILD}/${PLATFORM}/uDigInstallScript.nsi
        cp ${INSTALLER}/32-uninstallIcon.ico ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/LGPL.txt ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/udig/icons ${BUILD}/${PLATFORM}/icons
        
        cd ${BUILD}/${PLATFORM}
        makensis "-NOCD" uDigInstallScript.nsi
        cd ${HERE}
        cp ${BUILD}/${PLATFORM}/udig-*.exe ${BUILD}/udig-${VERSION}.${EXT}.exe
    else
        WINE=`which wine`
        if [ $? == 0 ] ; then
            cp installer/* ${BUILD}/${PLATFORM}/
            HERE=`pwd`
            if [ ! -f "${TOOL}" ] ; then
                TOOL=`find $HOME/.wine -name makensis.exe|sed -n 1p`
            fi
    
            if [ -f "${TOOL}" ] ; then
                cd ${BUILD}/${PLATFORM}
                $WINE "${TOOL}" "/NOCD" uDigInstallScript.nsi
                cd ${HERE}
                cp ${BUILD}/${PLATFORM}/udig-*.exe ${BUILD}/udig-${VERSION}.${EXT}.exe
            else
                echo "makensisw.exe cannot be found"
            fi
        else 
            echo "wine is not installed so creating windows installer"
        fi
    fi
}

function extract_jre () {

    if [[ $PLATFORM == linux* ]] ; then
        echo "Looking for ${JRE}/${PLATFORM_JRE}.tar.gz"
        if [ -f ${JRE}/${PLATFORM_JRE}.tar.gz ] ; then
            echo "Extracting ${JRE}/${PLATFORM_JRE}.tar.gz"
            tar xzf ${JRE}/${PLATFORM_JRE}.tar.gz -C ${BUILD}/${PLATFORM}/udig
        else
            echo "${JRE}/${PLATFORM_JRE}.tar.gz not found - user will require their own JRE"
            exit
        fi
    else
        echo "Looking for ${JRE}/${PLATFORM_JRE}.zip"
        if [ -f ${JRE}/${PLATFORM_JRE}.zip ] ; then
            echo "Extracting ${JRE}/${PLATFORM_JRE}.zip ..."
            unzip -q -d ${BUILD}/${PLATFORM}/udig ${JRE}/${PLATFORM_JRE}.zip
        else
            echo "${JRE}/${PLATFORM_JRE}.zip not found - user will require their own JRE"
        fi
    fi
}