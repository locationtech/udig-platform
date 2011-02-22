package net.refractions.udig.build.pack

import net.pragmaticdesign.scala.io._

abstract class Prepare(platform:Platform.Value, config:Configuration) {
  val out = config.out(platform)
  out.mkdirs

  protected val baseDir = config.in(platform)

  protected val jre = File(config.resources+"jre/"+platform+"/jre");

  def addJRE() {

    println("Copying exported application:" + baseDir.path +" to "+ out.path)
    baseDir.copyRecursive(out)

    val jreDest = out.newChildFile("udig")
    println("Copying jre:" + jre.path +" to "+ jreDest.path)
    jre.copyRecursive(jreDest)
  }

}

object Prepare {
  def runProcess( command:String ){
    println ("Executing command :"+command)
    def process = Runtime.getRuntime.exec(command)
    printStream(process.getInputStream())
  }

  def baseName(originalFile:File):String={
    val fileName = originalFile match {
    case zip:ArchiveEntry => zip.container.javaFile.getName
    case file => file.name
    }

    fileName match {
    case _ if( fileName.endsWith(".zip") ) => fileName.substring(0,fileName.length-".zip".length)
    case _ if( fileName.endsWith(".tar.gz") ) => fileName.substring(0,fileName.length-".tar.gz".length)
    case _  => fileName
    }
  }
  def zipResult(out:File, originalFile:File, from:String){
    val newName = baseName(originalFile)
    println("Zipping "+newName+".zip")

    val process = Runtime.getRuntime.exec("zip -r ../"+newName+".zip "+from, new Array[String](0), new java.io.File(out.absolutePath))
    printStream(process.getInputStream)

    out.deleteRecursive()

  }

  def printStream(stream:java.io.InputStream){
    var i:Int = stream.read();
    while (!i.equals(-1)){
      print( i.asInstanceOf[Char] )
      i=stream.read();
    }
  }

}
