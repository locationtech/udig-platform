package net.refractions.udig.build.pack

import scalax.io.Implicits._
import scalax.io.InputStreamResource
import java.io.File
import java.util.Properties
import scant.Ant
import scant.Ant.{Dir,copy,echo,antElem2Node}
import scala.maven.model.MavenProjectAdapter

class WindowsPrepare(params:Params) extends Prepare (params){
  
  
  def run(){
        addJRE()
        
        val eclipseDir = new File(tmpBuild,"eclipse")
        uDigFolder.renameTo(eclipseDir)

        val nsisFiles = new File(resources, "nsis-installer/")
        val icons = new File(nsisFiles,"icons")
        Ant( copy(Dir(icons.getAbsolutePath), Dir(eclipseDir.getAbsolutePath+"/icons/")) )

        val nsisScript = new File(tmpBuild, "uDigInstallScript.nsi")
        
        val licenseFile = new File(resources, "LICENSE.txt")
		    
		    val outLicense = new File(tmpBuild, "LICENSE.txt");
        licenseFile.copyTo( outLicense )
        
        writeNsisScript(nsisScript, nsisFiles)

        executeNsis(nsisScript)

        val installerOption = tmpBuild.listFiles.find( _.getName.endsWith("exe"))
        
        installerOption match {
            case Some(f) =>   { val name = f.getName.take( f.getName.indexOf(".exe") )+"-"+params.timestamp+".exe"
                                f.copyTo( new File( deploy, name ) )
                              }
            case None => null
        }
        
        tmpBuild.deleteRecursively()
        
  }
  
  def executeNsis(nsisScript:File){
        val command = {
            if( System.getProperty("os.name").toUpperCase.indexOf("WINDOWS") > -1 ) 
                params("makensis.win")
            else if( System.getProperty("os.name").toUpperCase.indexOf("MAC") > -1 ) 
                params("makensis.osx")
            else if( System.getProperty("os.name").toUpperCase.indexOf("LINUX") > -1 )
                params("makensis.linux")            
            else
                params("makensis.other")
        }
        
		val argc = command.split(",") ++ Array( 
                    	"uDigInstallScript.nsi" ).map( _.trim )
  
		println("Starting to run process: "+argc.mkString(", ")+" in directory "+nsisScript.getParentFile)
		
		val builder = new ProcessBuilder(argc).directory(nsisScript.getParentFile)
		builder.environment.put( "PATH", System.getenv("PATH"))
		val process = builder.redirectErrorStream(true).start
		
		InputStreamResource(process.getInputStream).lines.foreach( params.log.info _ )

		process.waitFor
		println("done process: "+argc.mkString(", "))
		
		if( process.exitValue!=0 ){
		  throw new AssertionError("Creating windows installer failed for parameters: "+argc.mkString(",")+" in directory: " + 
		      builder.directory+" in environment: "+builder.environment)
		}
    
  }
  
  def writeNsisScript(nsisScript:File, nsisFiles:File){
        val originalScript = new File(nsisFiles,"uDigInstallScript.nsi")
        val processedScript = originalScript.readLines.map( _.
			    replaceAll( "VersionXXX",params("version") ))
			
        nsisScript.writeLines( processedScript )
  }
  
}
