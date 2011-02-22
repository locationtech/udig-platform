package net.refractions.udig.build.pack

import net.pragmaticdesign.scala.io._
import net.pragmaticdesign.scala.io.BasicFile._


class WindowsPrepare( platform:Platform.Value, configuration:Configuration) extends
  Prepare (platform, configuration){


  def createExe(){
        addJRE()

        val eclipseDir = out.newChildFile("eclipse")
        out.newChildFile("udig").renameTo(eclipseDir)
        val nsisFiles = configuration.resources+"resources/nsis-installer/"
        val icons = File(nsisFiles+"icons")
        icons.copyRecursive(eclipseDir)
        val nsisScript = out.newChildFile("uDigInstallScript.nsi")

        val licenseFile = File(configuration.resources+"resources/LICENSE.txt")
        licenseFile.copy(out)

        writeNsisScript(nsisScript, nsisFiles)

        executeNsis(nsisScript)

        nsisScript.delete()
        eclipseDir.deleteRecursive()
        out.newChildFile("LICENSE.txt").delete()

        out.listFiles.find( _.name.endsWith(".exe") ) match {
        case Some(file) => {
          val newFile = out.parentFile.newChildFile(Prepare.baseName(baseDir)+".exe")
          println("copying "+file+" to "+newFile)
          file.renameTo(newFile)
        }
        case None =>
        }
  }

  def executeNsis(nsisScript:File){
		val argc = Array(
		  configuration.wine,
          configuration.makensis,
                    "uDigInstallScript.nsi" )

		val envc = Array("PATH="+System.getenv("PATH"))
		val process = Runtime.getRuntime().exec( argc, envc, BasicFile.basicFileToJavaFile(out.asInstanceOf[BasicFile]));

        Prepare.printStream(process.getInputStream)

  }

  def writeNsisScript(nsisScript:File, nsisFiles:String){
        def replaceTagsAndAddNewLine(line:String):String={
            val replaced = line match {
            case string if( string.contains("VersionXXX")) => string.replaceAll("VersionXXX", configuration.version)
            case string => string
            }

            replaced.replaceAll("\r", "")+"\n"

        }

        def iterable = File(nsisFiles+"uDigInstallScript.nsi").iterable(List('\n'))
        nsisScript.writeStrings(Stream.fromIterator( iterable.elements ).map(replaceTagsAndAddNewLine ) )
  }

}
