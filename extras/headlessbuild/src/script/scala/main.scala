import java.io.File
import scalax.io.Implicits._
import scalax.io.InputStreamResource
import scant._

// add the maven libs to the ant libs so targets defined in jars there can be picked up
// specifically the maven-ant-tasks
Ant.libs++=project.getCompileClasspathElements()

val targetParam = if (project("targets")==null || project("targets").length==0) project("target") 
                  else project("targets")

val targets = if( targetParam==null || project("targets").length==0 ){
		log.error("No targets defined for execution")
		exit(0)
	} else {
		for( target <- project("targets").split(",") ) yield{
			target.toLowerCase().trim match {
				case "sdk" => Sdk()
				case "app" => App()
				case "update" => Update()
				case "package" => Package()
				case "clean" => Clean()
				case x => Unknown(x)
			}
		}
}
val errors = targets.filter( _.isInstanceOf[Unknown] )

errors.foreach { unknown => log.error(unknown+" is not a recognized target") }
if( !errors.isEmpty ) exit(1)

val sharedProps = new File("shared.properties")
val appProps = new File("app.properties")
val sdkProps = new File("sdk.properties")

val ant = Project(xml.XML.loadFile("antScript.xml"))
val basedir = (ant.xml \ "@basedir").text
val buildProps = new File(basedir+"build.properties")
buildProps.deleteOnExit

if (errors.isEmpty) {
	
	targets.foreach{ target=>
		log.info("Running target: "+target)
		target.run(ant)
	}
}

// Target definitions
abstract class Target {
	val ANT_OPTS = if( project("ANT_OPTS")!=null ) 
  	project("ANT_OPTS").split(",")
  else 
  	new Array(0)
  
	def properties( specific:File ){		
		buildProps.delete()
		specific.copyTo(buildProps)
		sharedProps.inputStream.pumpTo(buildProps.appendOutputStream)
		
		// finds the PDE build and equinox Launcher plugins and their version
		// numbers if not defined in the properties.  
        val config = 	net.refractions.udig.build.pack.Package.resolveProperties(buildProps)
        val plugins= new File(config("eclipseLocation")+"/plugins")
        if( !new File(config("eclipseLocation")).exists ){
            throw new AssertionError("The eclipse location: "+new File(config("eclipseLocation"))+" does not exist")
        }
        def getMax(name:String):String = {
        	val matched = plugins.listFiles.filter( _.getName.startsWith(name) )

            ("" /: matched ){  (max,file)=>
                val end = file.getName.length - (if (file.getName.endsWith(".jar")) 4 else 0)
                val newVal = file.getName.substring(name.length,end)
        
                if( newVal > max ) newVal else max
           }
        }
    
        if( config.get("pdeBuildPluginVersion")==None ){
          val pdeBuildPluginVersion = getMax("org.eclipse.pde.build_")
          buildProps.appendOutputStream.writeLine("\npdeBuildPluginVersion="+pdeBuildPluginVersion)
        }
        if( config.get("equinoxLauncherPluginVersion")==None ){
          val equinoxLauncherPluginVersion = getMax("org.eclipse.equinox.launcher_")
          buildProps.appendOutputStream.writeLine("\nequinoxLauncherPluginVersion="+equinoxLauncherPluginVersion)
        }
        
        if(project("configs")!=null){
          buildProps.appendOutputStream.writeLine("configs="+project("configs"))    
        }
        if(project("offline")!=null){
          buildProps.appendOutputStream.writeLine("offline=true")    
        }
        
        // done finding launcher and pde build plugins
        if( ANT_OPTS.contains("-verbose") ){
          println(buildProps.slurp)
        }
	}
	
	def run( ant:Project)
}

case class Unknown(name:String) extends Target{
	override def run( ant:Project ) {
		null
	}
	
}

case class Update extends Target {
	override def run( ant:Project ) {
		val cmd = Array("svn", "up", basedir)
		log.info("executing: '"+cmd.mkString(" ")+"'")
		val process = new ProcessBuilder(cmd).redirectErrorStream(true).start()
		val in = InputStreamResource(process.getInputStream())
		in.lines.foreach( line => log.info( line ) )
		process.waitFor
		if( process.exitValue != 0 ){
			throw new org.apache.maven.plugin.MojoFailureException("There was a problem updating the repository.  Make sure you have the svn commandline tool installed")
		}
	}
}

case class Sdk extends Target {
//	def parseAdditionalPlugins(string:String):Seq[(String,List(String))]
	override def run( ant:Project ) {
		properties (sdkProps)
		val props = new java.util.Properties()
		props.load(new java.io.FileInputStream(buildProps) )
		//depends="pde-build"
		import scant.Ant.{Dir}
		val sdktarget = <target name="sdk-build"  depends="pde-build">		
			<property name="tmp" location="${buildDirectory}/${buildLabel}/tmp" />
			<property name="archive" location="${buildDirectory}/${buildLabel}/${archiveNamePrefix}.zip" />
			<copy toDir="${tmp}/${archivePrefix}" >
				{
					def splitTerm(term:String)={
						val split = term.split(":")
						(split(0),split(1).split(","))
					}
					for { 
						term <- props.getProperty("deltaPackPlugins").split(";") 
					} yield { 
						val (basedir,paths) = splitTerm(term)
						FileSet(Dir("${baseLocation}"), paths.map( _.trim ), Nil).xml
					}
				}
			</copy>
			<zip destfile="${archive}" update="true" duplicate="preserve" >
				{FileSet(Dir("${tmp}")).xml}
			</zip>
			<delete dir="${tmp}"/>
			
 		    <tstamp>
		        <format property="versionTimestamp" pattern="yyyyMMddHH" />
		    </tstamp>
		    
			<copy toDir="${deployDir}" file="${archive}">
    			<globmapper from="*.zip" to="*-${versionTimestamp}.zip"/>
			</copy>
		</target>
		( ant ++ sdktarget ).run( (Array("sdk-build") ++ ANT_OPTS):_* )
	}
}
case class App extends Target {
	override def run( ant:Project ) {
		properties (appProps)
		//println(ant.xml )
		ant.run((Array("pde-build") ++ ANT_OPTS):_*)
	}
}

case class Package extends Target {
	override def run( ant:Project ) {
		properties (appProps)
		net.refractions.udig.build.pack.Package(buildProps, project, log)
	}
}
case class Clean extends Target {
	override def run( ant:Project ) {
		properties (appProps)
		ant.run((Array("clean") ++ ANT_OPTS):_*)
	}
}
