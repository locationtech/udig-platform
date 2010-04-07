package net.refractions.udig.build.pack

import scalax.io.Implicits._
import java.io.File
import java.util.Properties
import scala.maven.model.MavenProjectAdapter
import org.apache.maven.plugin.logging.Log

case class Params(config:Map[String,String], project:MavenProjectAdapter, log:Log,
	file:File, timestamp:String, platform:Platform){
	    def this(config:Map[String,String], project:MavenProjectAdapter, log:Log, timestamp:String) =
	        this( config, project, log, null, timestamp, null)
	        
		def apply(key:String) = config(key)
		def copy(f:File, p:Platform) = Params(config, project, log, f, timestamp, p)
	}

abstract class Prepare(params:Params) {
  val deploy = new File(params("deployDir"))
  val tmpBuild = new File(deploy,".tmp")
  val resources = new File(params.project.getBasedir()+"/src/resources/")
  val uDigFolder = new File(tmpBuild, params("archivePrefix"))

  if( tmpBuild.exists ){
  	tmpBuild.deleteRecursively
  }

  tmpBuild.mkdirs
  params.log.info("unzipping original file "+params.file+" to "+tmpBuild+" \n")
  params.file.unzipTo(tmpBuild)

  protected val baseDir = params("buildType")+"."+params("buildId")

  def addJRE() {
    val jreBaseDir = new File(resources, "jre/"+params.platform.id)  
	val jre = jreBaseDir.listFiles().find( _.getName.endsWith(".zip") );
	
	jre match {
	    case Some(file) => {
            params.log.info("Copying jre:" + file +" to "+ uDigFolder)
            file.unzipTo(uDigFolder)
	    }
	    case None => params.log.error("A jre zip file is was expected in the "+jreBaseDir+" directory, but none was found" )
	}
  }
  
}
