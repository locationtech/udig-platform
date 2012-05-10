#!/bin/bash
# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
export BASE=`dirname $0`

# Release Configuration
export INSTALLER=${BASE}/installer
export TARGET=${BASE}/target
#export TARGET=${BASE}/../features/net.refractions.udig-product/target/products/
export VERSION=1.3-SNAPSHOT
export BUILD=${BASE}/build

# Tycho Build
export PRODUCT_TARGET=${BASE}/../features/net.refractions.udig-product/target
export PRODUCT_SDK_TARGET=${BASE}/../features/net.refractions.udig_sdk-product/target


# net.refractions.udig.libs "qualifier" for SDK (used to update libs source reference
# example: TAG=1.3.2
# example: QUALIFIER=1.3.2.201201031509
export TAG=1.3.1

# grab the qualifier after doing your target export; it is based on todays date
export QUALIFIER=1.3.2.201204302357

# Build Resources
export JRE=${BASE}/jre

export JRE_WIN32=jre1.6.0_25.win32_gdal_ecw
export JRE_WIN64=jre1.6.0.win64
export JRE_LIN32=jre1.6.0_25.lin32_gdal_ecw
export JRE_LIN64=jre1.6.0_25.lin64_gdal_ecw

echo "Release Version:  ${VERSION}"

echo "Staged for release by maven tycho build..."
ls ${PRODUCT_TARGET}/*.zip

echo "Staged SDK for release by maven tycho build..."
ls ${PRODUCT_SDK_TARGET}/*.zip

if [ ! -f ${TARGET}/udig-${VERSION}-sdk.zip ] 
then
    if [ -f ${PRODUCT_SDK_TARGET}/udig-${VERSION}-sdk-linux.gtk.x86.zip ] 
    then
        echo "Staging ${PRODUCT_SDK_TARGET}/udig-${VERSION}-sdk-linux.gtk.x86.zip for release as SDK"
        cp ${PRODUCT_SDK_TARGET}/udig-${VERSION}-sdk-linux.gtk.x86.zip ${TARGET}/udig-${VERSION}-sdk.zip
    fi
fi

echo "Staged for release in the target directory: ${TARGET}"
ls ${TARGET}/*.zip

echo "Available JREs:"
ls ${JRE}

echo "Expected Qualifier: ${QUALIFIER} checking for ${TARGET}/udig-${VERSION}-sdk.zip"
if [ -f ${TARGET}/udig-${VERSION}-sdk.zip ] 
then
    unzip -l ${TARGET}/udig-${VERSION}-sdk.zip | grep libs_
fi