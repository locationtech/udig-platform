# !/bin/sh

# by default, use the java executable on the path
vm=java

#set the DISPLAY for running tests on Linux
DISPLAY=:0.0;export DISPLAY

#this value must be set when using rsh to execute this script, otherwise the script will execute from the user's home directory
dir=.

# operating system, windowing system and architecture variables
os=
ws=
arch=

# list of tests (targets) to execute in test.xml
tests=

# default value to determine if eclipse should be reinstalled between running of tests
installmode="clean"

# name of a property file to pass to Ant
properties=

# message printed to console
usage="usage: $0 -os <osType> -ws <windowingSystemType> -arch <architecture> [-noclean] [<test target>][-properties <path>]"


# proces command line arguments
while [ $# -gt 0 ]
do
	case "$1" in
		-dir) dir="$2"; shift;;
		-os) os="$2"; shift;;
		-ws) ws="$2"; shift;;
		-arch) arch="$2"; shift;;
		-noclean) installmode="noclean";;
		-properties) properties="-propertyfile $2";shift;;
		-vm) vm="$2";shift;;
		*) tests=$tests\ $1;;
	esac
	shift
done

# for *nix systems, os, ws and arch values must be specified
if [ "x$os" = "x" ]
then
	echo >&2 "$usage"
	exit 1
fi

if [ "x$ws" = "x" ]
then
	echo >&2 "$usage"
	exit 1
fi

if [ "x$arch" = "x" ]
then
	echo >&2 "$usage"
	exit 1
fi

#necessary when invoking this script through rsh
cd $dir

# verify os, ws and arch values passed in are valid before running tests
if [ "$os-$ws-$arch" = "linux-motif-x86" ] || [ "$os-$ws-$arch" = "linux-gtk-x86" ] || [ "$os-$ws-$arch" = "solaris-motif-sparc" ] || [ "$os-$ws-$arch" = "solaris-gtk-sparc" ]|| [ "$os-$ws-$arch" = "aix-motif-sparc" ] || [ "$os-$ws-$arch" = "hpux-motif-ppc" ] || [ "$os-$ws-$arch" = "qnx-photon-x86" ]
then
	if [ ! -r eclipse ]
	then
		#unzip -qq -o eclipse-SDK-*.zip
		#unzip -qq -o -C eclipse-junit-tests-*.zip */plugins/org.eclipse.test*
		unzip -qq -o uDig.zip
		unzip -qq -o -C uDig-tests-*.zip */plugins/org.eclipse.test*
		unzip -qq -o -C uDig-tests-*.zip */plugins/org.eclipse.ant.core*
		unzip -qq -o -C uDig-tests-*.zip */plugins/org.eclipse.core.variables*
	fi

# run tests
$vm -cp eclipse/startup.jar -Dosgi.ws=$ws -Dosgi.os=$os -Dosgi.arch=$arch org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -file test.xml $tests -Dws=$ws -Dos=$os -Darch=$arch -D$installmode=true $properties -logger org.apache.tools.ant.DefaultLogger

# display message to user if os, ws and arch are invalid
else
	echo "The os, ws and arch values are either invalid or are an invalid combination"

exit 1
fi

