#!/bin/bash

# Requirements for this script to run
# The directory where this script is

printHelp()
{
  echo "do_release version [release_files_dir]"
  return
}

prepareMac()
{
  echo "Preparing Mac zip file"
  cd $FILES
  mkdir .mactmp
  cp $MACOSX .mactmp/
  mv $MACOSX $MACOSX.old
  cd .mactmp

  unzip -q $MACOSX
  rm $MACOSX

  echo "Restructuring zip file"
  cd udig
  mv -f configuration/Info.plist udig.app/Contents/Info.plist
  rm -rf configuration/iconsForScript/
  mv features udig.app/Contents/Resources/
  chmod 777 configuration
  chmod 777 configuration/config.ini
  mv configuration udig.app/Contents/Resources/
  mv plugins udig.app/Contents/Resources/

  cd ..
  zip -r -q $MACOSX udig
  cd ..
  mv .mactmp/$MACOSX ./
  rm -rf .mactmp
  cd $BIN
  return
}

prepareLinux()
{
  echo "Preparing Linux zip file"
  cd $FILES

#Add jre to the linux file
  echo "Adding jre to linux zip"
  cp $LINUX $LINUX.old
  unzip -q $LINUX
  cp -R $BIN/linux-jre $FILES/udig/jre
  zip -r -q $LINUX udig
  rm -rf udig

  cd $BIN
  return
}

prepareWindows()
{
  echo "Preparing Windows zip file"
  cp -R $BIN/win-installer $FILES/
  cd $FILES


  if [ -d "$FILES/eclipse" ]; then
    rm -rf $FILES/eclipse
  fi

  unzip -q $WINDOWS
  if [ -d "$FILES/udig" ]; then
    mv $FILES/udig $FILES/eclipse
  fi

  if [ -d "$FILES/uDig" ]; then
    mv $FILES/uDig $FILES/eclipse
  fi
  if [ -d "$FILES/UDIG" ]; then
    mv $FILES/UDIG $FILES/eclipse
  fi

  if [ ! -d "$FILES/eclipse" ]; then
    echo "Cannot create rename the root directory of uDig to $FILES/eclipse"
    exit 0;
  fi

  cp -R $FILES/win-installer/win-jre eclipse/jre
  cp -R $FILES/win-installer/icons eclipse/
  cp $FILES/win-installer/LGPL.txt ./
  cp $FILES/win-installer/README.txt ./

  echo "Version: $VERSION"
  sed "/VersionXXX/s//$VERSION/g" win-installer/uDigInstallScript.nsi > ./uDigInstallScript.nsi
  wine "$NSIS_HOME/makensisw.exe" ./uDigInstallScript.nsi
  rm -rf eclipse
  rm LGPL.txt
  rm uDigInstallScript.nsi
  rm README.txt
  rm -rf win-installer
  cd $BIN
  return
}

VERSION=$1

echo ""
if [ -z "$NSIS_HOME" ]; then
  echo "The NSIS_HOME environment variable is not defined"
  echo "This environment variable is needed to run this program"
  echo "NSIS_HOME must point to the NSIS installation.  Specifically the makensisw.exe must be in the directory"
  exit 1
fi

BIN=$RELEASE_HOME
if [ -z "$RELEASE_HOME" ]; then
  BIN=`pwd`
fi

if [ ! -d "$BIN/linux-jre" ]; then
  echo "$BIN does not contain a linux-jre directory"
  echo ""
  echo "Either the current directory or RELEASE_HOME must contain the directories: linux-jre and win-installer"
  echo "(win-installer contains win-jre, icons, README.txt, LGPL.txt and uDigInstallScript.nsi)"
  exit 1
fi
if [ ! -d "$BIN/win-installer" ]; then
  echo "$BIN does not contain a win-installer directory"
  echo ""
  echo "Either the current directory or RELEASE_HOME must contain the directories: linux-jre and win-installer"
  echo "(win-installer contains win-jre, icons, README.txt, LGPL.txt and uDigInstallScript.nsi)"
  exit 1
fi
if [ ! -d "$BIN/win-installer/win-jre" ]; then
  echo "$BIN/win-installer does not contain a win-jre directory"
  echo ""
  echo "Either the current directory or RELEASE_HOME must contain the directories: linux-jre and win-installer"
  echo "(win-installer contains win-jre, icons, README.txt, LGPL.txt and uDigInstallScript.nsi)"
  exit 1
fi
if [ ! -d "$BIN/win-installer/icons" ]; then
  echo "$BIN/win-installer does not contain an icons directory"
  echo ""
  echo "Either the current directory or RELEASE_HOME must contain the directories: linux-jre and win-installer"
  echo "(win-installer contains win-jre, icons, README.txt, LGPL.txt and uDigInstallScript.nsi)"
  exit 1
fi
if [ ! -f "$BIN/win-installer/icons/32-uDigIcon.ico" ]; then
  echo "$BIN/win-installer/icons does not contain 32-uDigIcon.ico"
  exit 1
fi
if [ ! -f "$BIN/win-installer/icons/32-uninstallIcon.ico" ]; then
  echo "$BIN/win-installer/icons does not contain 32-uninstallIcon.ico"
  exit 1
fi
if [ ! -f "$BIN/win-installer/README.txt" ]; then
  echo "$BIN/win-installer does not contain a README.txt file"
  exit 1
fi
if [ ! -f "$BIN/win-installer/LGPL.txt" ]; then
  echo "$BIN/win-installer does not contain a LGPL.txt file"
  exit 1
fi
if [ ! -f "$BIN/win-installer/uDigInstallScript.nsi" ]; then
  echo "$BIN/win-installer does not contain a uDigInstallScript.nsi file"
  exit 1
fi

if [ -z "$VERSION" ]; then
  echo "The version number is a required parameter"
  printHelp
  exit 1
fi

FILES=$2

if [ -z $2 ]; then
  FILES=`pwd`
fi

LINUX="udig-$1.linux.gtk.x86.zip"
WINDOWS="udig-$1.win32.win32.x86.zip"
MACOSX="udig-$1.macosx.carbon.x86.zip"

if [ ! -f "$FILES/$LINUX" ]; then
  echo "File $FILES/$LINUX must exist in directory"
  exit 1
fi
if [ ! -f $FILES/$WINDOWS ]; then
  echo "File $FILES/$WINDOWS must exist in directory"
  exit 1
fi
if [ ! -f $FILES/$MACOSX ]; then
  echo "File $FILES/$MACOSX must exist in directory"
  exit 1
fi


echo ""
echo "Release $1 in directory $FILES will now be created."
echo "Is this correct? (y/n)"
read go

if [ $go == "n" ]; then
  echo "Please rerun with correct information"
  exit 1
fi

#prepare the macosx file
#prepareMac

#prepare linux zip file
#prepareLinux

#prepare the windows installer
prepareWindows

