#!/bin/sh

echo "compiling required classes.."
fsc -sourcepath src -d bin src/net/refractions/udig/build/pack*.scala

echo "Running..."
scala -classpath bin net.refractions.udig.build.pack.Main
