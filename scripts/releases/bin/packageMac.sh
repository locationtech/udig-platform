#!/bin/sh

mkdir .mactmp
cp $1 .mactmp/
mv $1 $1.old
cd .mactmp

unzip $1
rm $1
cd udig
mv Info.plist udig.app/Contents/Info.plist
mv features udig.app/Contents/Resources/
chmod +777 configuration
chmod +777 configuration/config.ini
mv configuration udig.app/Contents/Resources/
mv plugins udig.app/Contents/Resources/
mv startup.jar udig.app/Contents/Resources/
rm udig.ini
cp udig.app/Contents/Resources/icon128.icns udig.app/Contents/

cd ..
zip -r $1 udig
cd ..
mv .mactmp/$1 ./
rm -rf .mactmp
