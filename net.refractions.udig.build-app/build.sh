#!/bin/sh

set -x
if [! -d bin]; then
  mkdir bin
fi

echo "Compiling source files"
fsc -d bin ../net.refractions.udig.build-package/src/net/refractions/udig/build/pack/*.scala
fsc -d bin src/net/refractions/udig/build/app/*.scala

echo "Executing..."
scala -classpath bin net.refractions.udig.build.app.Main $@
