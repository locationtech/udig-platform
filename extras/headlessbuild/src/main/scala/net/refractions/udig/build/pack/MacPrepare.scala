package net.refractions.udig.build.pack

import scalax.io.Implicits._
import java.io.File
import java.util.Properties
import scant.{Ant,Move,TarFileSet}
import scant.Ant.{Dir,copy,zip,echo,antElem2Node}
import scant.Tar._
import scant.Tar

import scala.maven.model.MavenProjectAdapter

class MacPrepare(params:Params) extends Prepare (params){

	private val plugins = new File(uDigFolder,"plugins")
	private val eclipseConfig = new File(uDigFolder,"configuration")
	private val features = new File(uDigFolder,"features")
	private val defaultAppName = new File(uDigFolder, params("archivePrefix")+".app")
	private val app = uDigFolder.listFiles.find( _.getName.endsWith(".app")).getOrElse(defaultAppName)
	private val imagery = new File(resources, "imagery")
	private val plist = new File(resources, "Info.plist")
	
	private val appResources = new File(app, "Contents/Resources/")
	private val appOS = new File( app, "/Contents/MacOS/")
      
	def run(){
	  
	  
      params.log.info( "\tWriting out plist");
      copyAndUpdatePlist()

      params.log.info( "\tWriting out udig.ini");
      copyAndUpdateUdigIni()
  
      val baseName = params.file.getName
      val tarFile = deploy.getAbsolutePath+"/"+baseName.take(baseName.lastIndexOf("."))+"-"+params.timestamp+".tar"
      val gzipFile = tarFile+".gz"
      new File(gzipFile).delete
	  Ant(
	  	echo("Moving "+plugins.getAbsolutePath+" to "+ appResources.getAbsolutePath),
  		Move(Dir(plugins.getAbsolutePath), Dir(appResources.getAbsolutePath) ),
  		echo("Moving "+eclipseConfig.getAbsolutePath+" to "+ appResources.getAbsolutePath),
  		Move(Dir(eclipseConfig.getAbsolutePath), Dir(appResources.getAbsolutePath) ),
  		echo("Moving "+features.getAbsolutePath+" to "+ appResources.getAbsolutePath),
  		Move(Dir(features.getAbsolutePath), Dir(appResources.getAbsolutePath) ),
  		copy(Dir(imagery.getAbsolutePath), Dir(appResources.getAbsolutePath) ),
      echo("adding "+appOS.getAbsolutePath.drop(tmpBuild.getAbsolutePath.length+1) +"/udig"+" to archive"),
      Tar(Ant.File(gzipFile), GNU, GZIP, 
        TarFileSet(Dir(tmpBuild.getAbsolutePath)),
        TarFileSet(Dir(tmpBuild.getAbsolutePath),
                  List(appOS.getAbsolutePath.drop(tmpBuild.getAbsolutePath.length+1) +"/udig"), 
                  Nil).mode("755"))  
	  )
        tmpBuild.deleteRecursively()
	}
	
    def copyAndUpdateUdigIni(){
       val ini = new File(appOS,"udig.ini")
       val removedmx = ini.readLines.filter( !_.trim().startsWith("-Xmx"))
		   ini.writeLines(removedmx ++ List("-Xmx386M","-Dosgi.parentClassloader=ext"))
    }

	// replace the tags in the Info.plist (for OSX)
	private lazy val launcherJar:String = {
		val found = plugins.listFiles.find( 
			file => {
				file.getName.startsWith("org.eclipse.equinox.launcher" ) && 
				file.getName.endsWith(".jar") 
			} )
  
		found match {
			case Some(file) => file.getName
			case None => throw new RuntimeException("Couldn't find launcher jar")
		}
	}
	
	private lazy val launcherBinary:String = {
	  
		val found = plugins.listFiles.find( _.getName.startsWith("org.eclipse.equinox.launcher.carbon.macosx_" ) )
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
	
	private def copyAndUpdatePlist(){
	  
		val plistOut = new File(appResources, "/../Info.plist")
        def replaceTags(s:String):String={
          s match {
				case launcher if ( launcher.contains("@launcher@") ) => launcher.replace("@launcher@", launcherJar ) 
				case binary if( binary.contains("@launcher_binary@") ) => binary.replace("@launcher_binary@", launcherBinary )
				case line => line
			}
        }
        
		
        plistOut write plist.slurp.replaceAll("@launcher@",launcherJar).replaceAll("@launcher_binary@",launcherBinary)
	}
}
