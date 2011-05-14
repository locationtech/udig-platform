#!/bin/bash
# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
export BASE=`dirname $0`

# Release Configuration
export TARGET=~/Desktop/target/1.2.1
export VERSION=1.2-SNAPSHOT
export BUILD=${BASE}/build

# net.refractions.udig.libs "qualifier" for SDK
export QUALIFIER=1.2.1.201102022248

# Build Resources
export JRE=~/Desktop/target/jre

export JRE_WIN32=jre1.6.0_25.win32_gdal_ecw
export JRE_WIN64=jre1.6.0.win64
export JRE_LIN32=jre1.6.0_25.lin32_gdal_ecw
export JRE_LIN64=jre1.6.0_20.lin64_gdal_ecw

echo "Release Version:  ${VERSION}"
echo "Available for release:"
ls ${TARGET}