#!/bin/bash
source ./versions.sh
function assemble() {
    PLATFORM=$1
    EXT=$2
    
    echo "Looking for ${PRODUCT_TARGET}/org.locationtech.udig-product-${EXT}.zip"
    
    # Release win32 if available
    if [ -f ${PRODUCT_TARGET}/org.locationtech.udig-product-${EXT}.zip ] 
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

            echo "Extracting ${PRODUCT_TARGET}/org.locationtech.udig-product-${EXT}.zip"
            unzip -q -d ${BUILD}/${PLATFORM}/udig ${PRODUCT_TARGET}/org.locationtech.udig-product-${EXT}.zip

            echo "Preparing ${BUILD}/${PLATFORM} with start up scripts and html files"
            prepare_resources

            echo "Assemble ${BUILD}/udig-${VERSION}.${EXT}.zip"
            cd ${BUILD}/${PLATFORM}
            zip -9 -X -r -q ../udig-${VERSION}.${EXT}.zip udig
        else 
           echo "Already Exists ${BUILD}/udig-${VERSION}.${EXT}.zip"
        fi
    else 
        echo "Unable to locate ${PRODUCT_TARGET}/org.locationtech.udig-product-${EXT}.zip"
        echo
        echo "Available for release in org.locationtech.udig_sdk-feature:"
        ls ${PRODUCT_TARGET}/*.zip | xargs -n1 basename
        echo
        echo "To generate use: mvn install -Pproduct"
    fi
    
}

function prepare_resources () {

    for opt in `find ../plugins/ -name .options` ; do cat $opt >> ${BUILD}/${PLATFORM}/udig/debug-options ; done
    
    sed -e "s/VersionXXXX/${VERSION}/g;s/SeriesXXXX/${SERIES}/g" ${BASE}/udig.html > ${BUILD}/${PLATFORM}/udig/udig-${VERSION}.html
    cp ${BASE}/epl-v10.html ${BUILD}/${PLATFORM}/udig
    cp ${BASE}/bsd3-v10.html ${BUILD}/${PLATFORM}/udig
    cp ${BASE}/notice.html ${BUILD}/${PLATFORM}/udig
    
    cat ../plugins/org.locationtech.udig.libs/.options >> ${BUILD}/${PLATFORM}/udig/.options
    mkdir ${BUILD}/${PLATFORM}/udig/dropins
    
    if [ ! -f ${BUILD}/udig-${VERSION}.html ]
    then
        sed -e "s/VersionXXXX/${VERSION}/g;s/SeriesXXXX/${SERIES}/g" ${BASE}/udig.html > ${BUILD}/udig-${VERSION}.html
    fi
         
    if [[ $PLATFORM == linux* ]] ; then
        cp udig.sh "${BUILD}/${PLATFORM}/udig"
        chmod 755 "${BUILD}/${PLATFORM}/udig/udig_internal"
        cp udig-clean.sh "${BUILD}/${PLATFORM}/udig"
        cp udig-debug.sh "${BUILD}/${PLATFORM}/udig"
    fi
    if [[ $PLATFORM == win* ]] ; then
        cp *.bat ${BUILD}/${PLATFORM}/udig
        mkdir -p ${BUILD}/${PLATFORM}/udig/icons
        cp ${INSTALLER}/*.ico ${BUILD}/${PLATFORM}/udig/icons/
        rm ${BUILD}/${PLATFORM}/udig/eclipsec.exe
        windows_installer
    fi
    if [[ $PLATFORM == mac* ]] ; then
        HERE=`pwd`
    	PLATFORMCONTENT="${HERE}/${BUILD}/${PLATFORM}"

        chmod 755 "${PLATFORMCONTENT}/udig/udig_internal.app/Contents/MacOS/udig_internal"
        mv "${PLATFORMCONTENT}/udig/udig_internal.app" "${PLATFORMCONTENT}/udig/udig.app"
        
        # add in -data ~/udig-workspace to Info.plist
        sed -e "s/\<\string\>-showlocation\<\/string\>/<\string\>-showlocation\<\/string\>\<string\>-data<\/string>\\<string\>~\/udig-workspace\<\/string\>/g" "${PLATFORMCONTENT}/udig/udig.app/Contents/Info.plist" > "${PLATFORMCONTENT}/udig/udig.app/Contents/Info2.plist"
        rm "${PLATFORMCONTENT}/udig/udig.app/Contents/Info.plist"
        mv "${PLATFORMCONTENT}/udig/udig.app/Contents/Info2.plist" "${PLATFORMCONTENT}/udig/udig.app/Contents/Info.plist"
        
        rm "${PLATFORMCONTENT}/udig/udig_internal"
        ln -s "${PLATFORMCONTENT}/udig/udig.app/Contents/MacOS/udig_internal" "${PLATFORMCONTENT}/udig/udig"
        cp "${HERE}/mac-udig-clean.sh" "${PLATFORMCONTENT}/udig/udig-clean.sh"
        cp "${HERE}/mac-udig-debug.sh" "${PLATFORMCONTENT}/udig/udig-debug.sh"
        mv "${PLATFORMCONTENT}/udig/.options" "${PLATFORMCONTENT}/udig/udig.app/Contents/MacOS/"
        
        make_dmg_pretty
    fi
}

function windows_installer () {
    MAKENSIS=`which makensis`
    if [ $? == 0 ] ; then
        HERE=`pwd`
        # todo use sed or something to update VERSION inside uDigInstallScript.nsi
        
        cp ${INSTALLER}/* ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/*.txt ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/*.bmp ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/udig/icons ${BUILD}/${PLATFORM}/icons
        
        sed -e "s/VersionXXXX/${VERSION}/g" ${INSTALLER}/uDigInstallScript.nsi > ${BUILD}/${PLATFORM}/uDigInstallScript.nsi
        
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
            echo "wine is not installed so not creating windows installer"
        fi
    fi
}


function make_dmg () {
    echo -n "Mac packaging utilities available... "
	DMGTOOLS=`which hdiutil`
    if [ $? == 0 ] ; then
		echo "YES!"
		echo -n "Building MacOS DMG for product"
		#hdiutil create -fs HFS+ -volname "udig-${VERSION}" -srcfolder "${PLATFORMCONTENT}" "${PLATFORMCONTENT}/../udig-${VERSION}.${EXT}.dmg"
		hdiutil create -fs HFS+ \
		    -volname "udig-${VERSION}" \
		    -srcfolder "${PLATFORMCONTENT}"\
		    "${PLATFORMCONTENT}/../udig-${VERSION}.${EXT}.dmg"
	else
		echo "NO"
	fi
}
# credit to stackexchange and geoserver for dmg example
function make_dmg_pretty () {
    echo -n "Mac packaging utilities available... "
	DMGTOOLS=`which hdiutil`
    if [ $? == 0 ] ; then
		echo "YES!"
		
		VOL=udig-${VERSION}.${EXT}
        if [ -d "/Volumes/${VOL}" ]; then
		  # unmount VOL from a previous run        
          umount "/Volumes/${VOL}"
          check $? "unmount ${VOL}"
        fi
        DMG_TMP="${BUILD}/tmp-${VOL}.dmg"
        DMG_FINAL="${BUILD}/${VOL}.dmg"
        DMG_BACK="background.png"
        
        # remove previous run
        if [ -f "${DMG_TMP}" ]; then
          rm -f "${DMG_TMP}"
          check $? "rm temp dmg file"
        fi
        if [ -f "${DMG_FINAL}" ]; then
          rm -f "${DMG_FINAL}"
          check $? "rm dmg dmg file"
        fi
        
        echo -n "Building Temp MacOS DMG..."
        hdiutil create \
            -srcfolder "${PLATFORMCONTENT}"  \
            -volname "${VOL}" \
            -fs HFS+ \
            -fsargs "-c c=64,a=16,e=16" \
            -format UDRW \
            "${DMG_TMP}"
        check $? "dmg build"

        # mount volume so we can copy in background to make it pretty
        sleep 5
        device=$(hdiutil attach -readwrite -noverify -noautoopen "${DMG_TMP}" | egrep '^/dev/' | sed 1q | awk '{print $1}')
        sleep 5
        echo "DEVICE: ${device}"
        
        mkdir "/Volumes/${VOL}/.background"
        cp "${INSTALLER}/${DMG_BACK}" "/Volumes/${VOL}/.background/"        
        check $? "copy background img"
        
        # layout the icons
        echo "Using apple script to position icons and register background image..."
        ln -sf /Applications /Volumes/${VOL}/Applications
        
        dmg_width=500
        dmg_height=320
        dmg_topleft_x=200
        dmg_topleft_y=200
        dmg_bottomright_x=`expr $dmg_topleft_x + $dmg_width`
        dmg_bottomright_y=`expr $dmg_topleft_y + $dmg_height`
        
        echo '
           tell application "Finder"
             tell disk "'${VOL}'"
                   open
                   set current view of container window to icon view
                   set toolbar visible of container window to false
                   set statusbar visible of container window to false
                   set the bounds of container window to {'${dmg_topleft_x}', '${dmg_topleft_y}', '${dmg_bottomright_x}', '${dmg_bottomright_y}'}
                   set theViewOptions to the icon view options of container window
                   set arrangement of theViewOptions to not arranged
                   set icon size of theViewOptions to 96
                   set background picture of theViewOptions to file ".background:'${DMG_BACK}'"
                   set position of item "'udig'" of container window to {150, 100}
                   set position of item "'Applications'" of container window to {350, 100}
                   close
                   open
                   update without registering applications
                   delay 5
                   eject
                   delay 5
             end tell
           end tell
        ' | osascript

        # compress tmp
        echo -n "Compressing Final MacOS DMG..."
        hdiutil convert "${DMG_TMP}" -format UDZO -imagekey zlib-level=9 -o "${DMG_FINAL}"
        check $? "dmg compressing"
        
        if [ -f "${DMG_TMP}" ]; then
          rm -f "${DMG_TMP}"
        fi
	else
		echo "NO"
	fi
}

# Checks return value, allowing us to supply a better error message
function check() {
  if [ $1 -gt 0 ]; then
    echo "$2 failed with return value $1"
    exit 1
  else
    echo "$2 succeeded return value $1"
  fi
}
