#!/bin/bash
# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
export BASE=`dirname $0`

# Release Configuration
export INSTALLER="${BASE}/installer"
#export TARGET=${BASE}/target
#export TARGET=${BASE}/../features/org.locationtech.udig-product/target/products/
export SERIES=2.3
export VERSION=2.3.0.RC1
export BUILD="${BASE}/build"

# Tycho Build
export PRODUCT_TARGET="${BASE}/../features/org.locationtech.udig-product/target/products"

# Tycho SDK Build
export SDK_TARGET="${BASE}/../features/org.locationtech.udig_sdk-feature/target"

# echo "Staged for release by maven tycho build..."
# ls ${PRODUCT_TARGET}/*.zip | xargs -n1 basename
