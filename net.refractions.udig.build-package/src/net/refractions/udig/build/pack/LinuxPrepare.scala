package net.refractions.udig.build.pack

import net.pragmaticdesign.scala.io._

class LinuxPrepare(platform:Platform.Value, configuration:Configuration)  extends
  Prepare (platform, configuration){

  def createZip() {
    super.addJRE()
    val udig = out.newChildFile("/udig")
    val oldExecutable = udig.newChildFile("/udig")
    val executable = udig.newChildFile("/private-bin")
    oldExecutable.renameTo(executable);
    val oldini = udig.newChildFile("/udig.ini")
    val ini = udig.newChildFile("/private-bin.ini")
    oldini.renameTo(ini)

    val startScript = udig.newChildFile("udig")

	val script = """|#!/bin/sh
	|PRG="$0"
	|while [ -h "$PRG" ]; do
	|	ls=`ls -ld "$PRG"`
	|	link=`expr "$ls" : '.*-> \(.*\)$'`
	|	if expr "$link" : '/.*' > /dev/null; then
	|		PRG="$link"
	|	else
	|		PRG=`dirname "$PRG"`/"$link"
	|	fi
	|done
	|
	|# Get standard environment variables
	|PRGDIR=`dirname "$PRG"`
	|DATA_ARG=false
	|
	|for ARG in $@
	|do
	|	if [ $ARG = "-data" ]; then DATA_ARG=true; fi
	|done
	|
	|if $DATA_ARG; then
	|	$PRGDIR/private-bin $@
	|else
	|	$PRGDIR/private-bin -data ~/uDigWorkspace $@
	|fi
	|"""

    startScript.writeStrings(List(script.stripMargin))

    Prepare.runProcess("chmod +x "+executable+" "+udig+"/jre/bin/java "+startScript)
    Prepare.zipResult(out, baseDir, "udig/")
  }

}
