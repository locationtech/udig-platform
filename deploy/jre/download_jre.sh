#!/bin/bash


################################################################################
# Download prebuilt JREs from http://udig.refractions.net/files/downloads/jre/ #
#                                                                              #
# There is a manual process for assembling your own JRE, or look at            #
# linux_jre_build.sh for an automated example                                  # 
#                                                                              #
# Each JRE has JAI and ImageIO installed as a JRE extension, since the         #

################################################################################

# Build Resources
export DOWNLOAD=http://udig.refractions.net/files/downloads/jre
export JRE="${BASE}/jre"
export JRE_WIN32=jre1.6.0_25.win32_gdal_ecw
export JRE_WIN64=jre1.6.0.win64
export JRE_LIN32=jre1.6.0_25.lin32_jai_gdal_ecw
export JRE_LIN64=jre1.6.0_25.lin64_gdal_ecw

if [[ ! -e "${JRE_LIN64}.tar.gz" ]]; then
	echo "Attempting to download ${JRE_LIN64}."
	wget --progress=bar ${DOWNLOAD}/${JRE_LIN64}.tar.gz
	if [[ $? -ne 0 ]]; then
		echo "Unable to download ${JRE_LIN64} bundle from Refractions."
		exit
	fi
fi

if [[ ! -e ${JRE_LIN32}.tar.gz ]]; then
	echo "Attempting to download ${JRE_LIN32}."
	wget --progress=bar ${DOWNLOAD}/${JRE_LIN32}.tar.gz
	if [[ $? -ne 0 ]]; then
		echo "Unable to download ${JRE_LIN32} bundle from Refractions."
		exit
	fi
fi

if [[ ! -e ${JRE_WIN32}.zip ]]; then
	echo "Attempting to download ${WIN_LIN32}."
	wget --progress=bar ${DOWNLOAD}/${JRE_WIN32}.zip
	if [[ $? -ne 0 ]]; then
		echo "Unable to download ${JRE_WIN32} bundle from Refractions."
		exit
	fi
fi

if [[ ! -e ${JRE_WIN64}.zip ]]; then
	echo "Attempting to download ${WIN_LIN64}."
	wget --progress=bar ${DOWNLOAD}/${JRE_WIN64}.zip
	if [[ $? -ne 0 ]]; then
		echo "Unable to download ${JRE_WIN64} bundle from Refractions."
		exit
	fi
fi