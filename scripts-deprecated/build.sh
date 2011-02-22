#!/bin/sh

if [ "$1" = "" ]; then
	echo "usage: $0 <URL>";
	echo "URL: svn url to root of project (ie. directory containg 'plugins', and 'features' directories)"
	exit;
fi;

URL=$1

# clean last build
ant -f gen.xml clean
ant -f clean.xml

# checkout/update plugins, features, and fragments from repository
if [ ! -e build ]; then
	echo "Creating build directory"
  mkdir build
fi

if [ -e build/plugins ]; then
	echo "updating plugins"
  svn update build/plugins
else
	echo "checking out plugins"
  cd build
  svn checkout ${URL}/plugins
  cd ..
fi

if [ -e build/features ]; then
	echo "updating features"
  svn update build/features
else
	echo "checking out features"
  cd build
  svn checkout ${URL}/features
  cd ..
fi

if [ -e build/fragments ]; then
	echo "updating fragments"
  svn update build/fragments
else
	echo "checking out fragments"
  cd build
  svn checkout ${URL}/fragments
  cd ..
fi

if [ -e build/doc ]; then
	echo "updating fragments"
  svn update build/doc
else
	echo "checking out fragments"
  cd build
  svn checkout ${URL}/doc
  cd ..
fi

if [ $? != 0 ] || [ ! -e build/plugins ] || [ ! -e build/features ] || [ ! -e build/fragments ] || [ ! -e build/doc ]
then
	echo "ERROR: Unable to checkout out source from repostory"
	exit
fi

# read eclipse home from build.properties
ECLIPSE_HOME=`grep "^ *baseLocation=" build.properties`
ECLIPSE_HOME=${ECLIPSE_HOME:13}

echo $ECLIPSE_HOME
# fire up the build
java -cp ${ECLIPSE_HOME}/startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbuilder=. #-verbose

r=$?

ant -f gen.xml log
ant -f log.xml

return $r

