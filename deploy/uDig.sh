#!/bin/bash
JRE=$JAVA_HOME/jre/lib/ext
JAI_CP=$JRE/clibwrapper_jiio.jar:$JRE/jai_codec.jar:$JRE/jai_core.jar:$JRE/jai_imageio.jar:$JRE/mlibwrapper_jai.jar

java -Xbootclasspath/a:$JAI_CP -Dosgi.splashLocation=splash/splash.bmp -classpath startup.jar org.eclipse.core.launcher.Main -application net.refractions.udig.ui.uDig -os linux -ws gtk -arch x86 -nl en_US
