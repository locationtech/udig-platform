package net.refractions.udig.build.pack

import net.pragmaticdesign.scala.io._

class MacPrepare(platform:Platform.Value, configuration:Configuration) {

    private val out = configuration.out(platform)
    private val baseDir = configuration.in(platform)
	out.mkdirs

	private val plugins = baseDir.newChildFile("plugins")
	private val eclipseConfig:File = baseDir.newChildFile("configuration")
	private val features:File = baseDir.newChildFile("features")
	private val app:File = baseDir.newChildFile("udig.app")
	private val imagery:File = File(configuration.resources+"resources/imagery")
	private val plist:File = File(configuration.resources+"resources/Info.plist")

	private val macOut = out.newChildFile(platform.toString)
	private val resources = macOut.newChildFile("udig.app/Contents/Resources/")

	def createBundle() {

	  println ("Starting to package: " + baseDir);

      if( macOut.exists) {
        println("\tdeleting old data")
        macOut.deleteRecursive()
      }

	  println ("\tCopying "+platform+" application to "+macOut.path)
	  app.copyRecursive(file=>{!file.name.equals("Info.plist") & !file.name.equals("udig.ini")}, macOut)

      println ("\tCopying plugins to "+ resources.path)
	  plugins.copyRecursive(resources)
      println ("\tCopying configuration to "+ resources.path)
      eclipseConfig.copyRecursive(resources)
      println ("\tCopying features to "+ resources.path)
      features.copyRecursive(resources)

      val command = "chmod +x "+ macOut.absolutePath+"/udig.app/Contents/MacOS/udig"
      println("\tExecuting following command: '"+command+"'")
	  Runtime.getRuntime.exec(command)

      println( "\tWriting out plist");
      copyAndUpdatePlist()

      println( "\tWriting out udig.ini");
      copyAndUpdateUdigIni()

      println( "\tCopying images used by application bundle");
      imagery.eachChild( file=>{file.copy(resources.newChildFile(file.name))} )

      Prepare.zipResult(out, baseDir, macOut.name)
	}

    def copyAndUpdateUdigIni(){
       val out = resources.newChildFile("/../MacOS/udig.ini")
       val in = app.newChildFile("/Contents/MacOS/udig.ini")

       val newVal:Iterable[String] = in.iterable(List('\n','\r')).filter( !_.trim().startsWith("-Xmx")) ++
                                                   List("-Xmx386M","-Dosgi.parentClassloader=ext")
       out.writeStrings(newVal.map( _+"\n"))
    }
	// replace the tags in the Info.plist (for OSX)
	private lazy val launcherJar:String = {
		val found = plugins.listFiles.find(
			file => {
				file.name.startsWith("org.eclipse.equinox.launcher" ) &&
				file.name.endsWith(".jar")
			} )

		found match {
			case Some(file) => file.name
			case None => throw new RuntimeException("Couldn't find launcher jar")
		}
	}

	private lazy val launcherBinary:String = {

		val found = plugins.listFiles.find( _.name.startsWith("org.eclipse.equinox.launcher.carbon.macosx_" ) )
		val fragment = found match {
			case Some(file) => file
			case None => throw new RuntimeException("Couldn't find launcher mac fragment")
		}
		val binFound = fragment.listFiles.find( _.name.endsWith("so") )

		binFound match {
			case Some(bin) => fragment.name+"/"+bin.name
			case None => throw new RuntimeException("Couldn't find launcher mac fragment")
		}

	}

	private def copyAndUpdatePlist(){

		val plistOut = resources.newChildFile("/../Info.plist")
        def replaceTags(s:String):String={
          s match {
				case launcher if ( launcher.contains("@launcher@") ) => launcher.replace("@launcher@", launcherJar )
				case binary if( binary.contains("@launcher_binary@") ) => binary.replace("@launcher_binary@", launcherBinary )
				case line => line
			}
        }

        plistOut.writeStrings(plist.iterable(List('\n','\r')).map(replaceTags))
	}
}
