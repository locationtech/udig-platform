#!/bin/bash

################################################################################
# This script will build the linux32 jre required for uDig binary releases.    #
#                                                                              #
# The versions of various tasty bits are defined in the exports below.  The    #
# one thing this script will not do is download or install the jre, simply     #
# because there's a clickthrough license that I didn't feel like sorting out.  #
# As a result, you need to download and install the jre into the folder the    #
# script is running in.  Other things need to be valid versions.  And the      #
# imageie-ext bits need to be downloaded, since the folder structure isn't     #
# predictable between versions.  If things are already downloaded, they won't  #
# be refetched.                                                                #
################################################################################

export jre_version=1.6.0_18
export jai_version=1_1_3
export imageio_version=1_1
export imageioext_version=1.0.5
export gdal_data_version=1.4.5
export input=''

if [[ ! -e jre${jre_version} ]]; then
	echo "Please install the Sun JRE version ${jre_version} before building the jre package."
	exit
fi

if [[ ! -e jai-${jai_version} ]]; then
	if [[ ! -e jai-${jai_version}-lib-linux-i586.tar.gz ]]; then
		echo "Attempting to download jai version ${jai_version}."
		wget --progress=bar http://download.java.net/media/jai/builds/release/${jai_version}/jai-${jai_version}-lib-linux-i586.tar.gz
		if [[ $? -ne 0 ]]; then
			echo "Unable to download jai version ${jai_version} from Sun.  Please download the package manually before trying again."
			exit
		fi
	fi
	echo "Extracting jai verison ${jai_version}."
	tar -xzf jai-${jai_version}-lib-linux-i586.tar.gz
fi

if [[ ! -e jai_imageio-${imageio_version} ]]; then
	if [[ ! -e jai_imageio-${imageio_version}-lib-linux-i586.tar.gz ]]; then
		echo "Attempting to download jai-imageio version ${imageio_version}."
		wget --progress=bar http://download.java.net/media/jai-imageio/builds/release/1.1/jai_imageio-1_1-lib-linux-i586.tar.gz
		if [[ $? -ne 0 ]]; then
			echo "Unable to download jai-imageio version ${imageio_verison} from Sun.  Please download the package manually before trying again."
			exit
		fi
	fi
	echo "Extracting jai-imageio version ${imageio_version}."
	tar -xzf jai_imageio-${imageio_version}-lib-linux-i586.tar.gz
fi

if [[ ! -e imageioext-${imageioext_version} ]]; then
	if [[ ! -e imageio-ext-${imageioext_version}-linux32-mrsid-ecw-lib.tar.gz ]]; then
		echo "The imageio-ext package version ${imageioext_version} cannot be downloaded automatically.  Please download it manually before trying again."
		exit
	fi
	echo "Extracting imageio-ext version ${imageioext_version}."
	mkdir imageioext-${imageioext_version}
	cd imageioext-${imageioext_version}
	tar -xzf ../imageio-ext-${imageioext_version}-linux32-mrsid-ecw-lib.tar.gz
	cd ..
fi

if [[ ! -e gdal_data-${gdal_data_version} ]]; then
	if [[ ! -e gdal_data-${gdal_data_version}.zip ]]; then
		echo "The imageio-ext gdal_data package version ${gdal_data_version} cannot be downloaded automatically. Please download it manually before trying again."
		exit
	fi
	echo "Extracting imageio-ext gdal_data version ${gdal_data_version}."
	mkdir gdal_data-${gdal_data_version}
	cd gdal_data-${gdal_data_version}
	unzip -qq ../gdal_data-${gdal_data_version}.zip
	cd ..
fi

echo "Building uDig jre version ${jre_version}."
cd jre${jre_version}

# Copy in the JAI bits as follows:
#    jar's into lib/ext
#    so's into lib/i386
#    license stuffs into root
find ../jai-${jai_version}/ -name '*.jar' -exec cp {} lib/ext/ \;
find ../jai-${jai_version}/ -name '*.so' -exec cp {} lib/i386/ \;
find ../jai-${jai_version}/ -name '*.txt' -exec cp {} . \;

# Copy in the JAI Image IO bits as follows:
#    jar's into lib/ext
#    so's into lib/i386
#    license stuffs into root
find ../jai_imageio-${imageio_version}/ -name '*.jar' -exec cp {} lib/ext/ \;
find ../jai_imageio-${imageio_version}/ -name '*.so' -exec cp {} lib/i386/ \;
find ../jai_imageio-${imageio_version}/ -name '*.txt' -exec cp {} . \;

# Copy in the Image IO-ext bits indo lib/i386
cp ../imageioext-${imageioext_version}/* lib/i386/

cd ..

cp -R jre${jre_version} jre
cp -R gdal_data-${gdal_data_version} gdal_data

tar -czf jre${jre_version}.linux.x86_gdal_ecw_mrsid.tar.gz jre/ gdal_data/

echo "Cleaning up stuff."

rm -rf jai-${jai_version}/ jai_imageio-${imageio_version}/ imageioext-${imageioext_version}/ gdal_data-${gdal_data_version}/ jre/ gdal_data/

