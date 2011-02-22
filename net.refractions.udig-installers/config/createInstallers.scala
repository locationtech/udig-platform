#!/bin/sh
set -x
export LIBS=../installer-framework/lib


exec scala -classpath $LIBS/standalone-compiler.jar "$0" "$@"

!#

import net.pragmaticdesign.scala.io.File

class InstallerFactory(xml:File, readersWriters:Map[String,Installers.IValue]) {

  def createInstallers() {
    try{
    	xml.eachLine( line => copyAndReplace(line.mkString) )
    } finally {
    	for( installer <- readersWriters.values; writer = installer.writer ){
          writer.close()
        }
    }
  }

  def copyAndReplace( line:String ) {
    for( pack <- readersWriters.keySet;
    	 installer = readersWriters(pack);
         writer = installer.writer ) {
      val toWrite = {
        if( line.contains( "<!--@OS_DEPENDENT_PACKS@-->") )
          pack
      else
          line
      }

      writer write (toWrite)
      writer write ("\n")
    }

  }

}

class CmdlinePackagerListener extends com.izforge.izpack.compiler.PackagerListener
{
  val MSG_DEBUG = 0;
  val MSG_ERR = 1;
  val MSG_INFO = 2;
  val MSG_VERBOSE = 3;
  val MSG_WARN = 4;

    def packagerMsg(info:String) {
        packagerMsg(info, MSG_INFO);
    }

    def packagerMsg(info:String, priority:Int){
        val prefix = priority match {
        case MSG_DEBUG => "[ DEBUG ] "
        case MSG_ERR => "[ ERROR ] "
        case MSG_WARN => "[ WARNING ] "
        case _ => ""
        }

        println(prefix + info)
    }

    def packagerStart() {
        println("[ Begin ]")
        println()
    }

    def packagerStop() {
        println();
        println("[ End ]")
    }

}

object Installers extends Enumeration {

  val outputDir = new java.io.File("tmp/")
  outputDir.mkdirs()
  val build = new java.io.File("../installers")
  build.mkdirs


  val MAC = new IValue("Intel Mac", "macosx.carbon.x86", "mac-packs.xml", outputDir+"/mac-install.xml")
  val LIN32 = new IValue("Linux x86", "linux.gtk.x86", "linux-packs.xml", outputDir+"/lin-install.xml")
  val WIN = new IValue("Windows", "win32.win32.x86", "windows-packs.xml", outputDir+"/win-install.xml")

  class IValue(name:String, cplatform:String, configuration:File , cinstallerXML:String) extends Val(nextId, name) {
    nextId=nextId+1

    val installerXML = cinstallerXML
    val installerFile = new java.io.File(build+"/"+installerXML.substring(outputDir.getPath.length, installerXML.length - 3) + "jar")
    val platform = cplatform
    lazy val writer = newWriter

    def newWriter() = new java.io.FileWriter( installerXML )

	def read( ):String = {
	   val data=new StringBuilder
	   configuration eachLine (line => { data ++= line; data+='\n' })
	   data.toString
	}

  }

}

def createMacInstallerApp() {
    val installer = Installers.MAC
	File.fromString("installer.app").copyRecursive(file=>{!file.getName.startsWith(".")}, Installers.build)
	val newLocation = new java.io.File(Installers.build, "installer.app/Contents/Resources/Java/"+installer.installerFile.getName())
	newLocation.getParentFile.mkdirs
	installer.installerFile.renameTo( newLocation )
	Runtime.getRuntime.exec("chmod +x "+new java.io.File( Installers.build, "installer.app/Contents/MacOS/JavaApplicationStub").getAbsolutePath )
}

def cleanup() {
	new File( new java.io.File("tmp") ).deleteRecursive()
}

def checkPostConditions() {
	def file( string:String ):java.io.File=new java.io.File( string )

	for( installer <- Installers ) {
		val platform = installer.asInstanceOf[Installers.IValue].platform
		if( !file("export/"+platform).exists ){
			throw new RuntimeException("The export for the platform: "+platform+" does not exist in the export directory")
		}
	}
	if( file("jre").listFiles.length==0 ){
		throw new RuntimeException("The Java Runtimes are missing")
	}
}

//  ----------  script starts here  ------------  //
try{
	checkPostConditions()

	new File(Installers.build).deleteRecursive()

// replace the tags in the Info.plist (for OSX)
def findLauncherJar():String = {
	val found = new java.io.File("export/macosx.carbon.x86/udig/plugins").listFiles.find(
		file => {
			file.getName.startsWith("org.eclipse.equinox.launcher" ) &&
			file.getName.endsWith(".jar")
		} )
	found match {
		case Some(file) => file.getName
		case None => throw new RuntimeException("Couldn't find launcher jar")
	}
}

def findLauncherBinary():String = {
	val found = new java.io.File("export/macosx.carbon.x86/udig/plugins").listFiles.find( _.getName.startsWith("org.eclipse.equinox.launcher.carbon.macosx_" ) )
	val fragment = found match {
		case Some(file) => file
		case None => throw new RuntimeException("Couldn't find launcher mac fragment")
	}
	val binFound = fragment.listFiles.find( _.getName.endsWith("so") )

	binFound match {
		case Some(bin) => fragment.getName+"/"+bin.getName
		case None => throw new RuntimeException("Couldn't find launcher mac fragment")
	}

}

val plistIn:File = "installer-resources/Info.plist"
val plistOut = new java.io.File("tmp/Info.plist")
plistOut.getParentFile.mkdirs
plistOut.createNewFile
val plistWriter =  new java.io.FileWriter(plistOut)

plistIn eachLine (
	line => {
		val toWrite = line.mkString match {
			case launcher if ( launcher.contains("@launcher@") ) => launcher.replace("@launcher@", findLauncherJar() )
			case binary if( binary.contains("@launcher_binary@") ) => binary.replace("@launcher_binary@", findLauncherBinary() )
			case line => line
		}
	plistWriter.write(toWrite+"\n")
	})

	plistWriter.close();

// Now generate the installer.xml files for compiling the installers
val platforms = Map(
    Installers.MAC.read() -> Installers.MAC,
    Installers.LIN32.read() -> Installers.LIN32,
    Installers.WIN.read() -> Installers.WIN )

val installer = new InstallerFactory( "install.xml", platforms )
installer.createInstallers

Installers.build.mkdirs

// now run IZPack on the installer.xml files to generate the installers.
for (installer <- Installers){
  import com.izforge.izpack.compiler._

  Compiler.setIzpackHome("../installer-framework/");

  // Calls the compiler
  val listener = new CmdlinePackagerListener();
  val installerXML = installer.asInstanceOf[Installers.IValue].installerXML

  val out = installer.asInstanceOf[Installers.IValue].installerFile
  val compiler = new CompilerConfig(installerXML, "./", "standard", out.getPath(),
      "default", -1, listener, null);
  compiler.executeCompiler();

  while (compiler.isAlive())
    Thread.sleep(100);

  if (!compiler.wasSuccessful()) {
    println ("Compilation of "+installer+" failed")
    cleanup()
    System.exit(1)
  }

}

createMacInstallerApp()

cleanup()
} catch {
	case t => {
		if( args.exists( "-debug".equals ) ) {
			t.printStackTrace()
		}else{
			println( t.getMessage )
		}
	}
}
