package net.refractions.udig.build.app

import Logging._
import java.io.File
import java.io.InputStream
import java.io.BufferedInputStream
import java.io.OutputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URLClassLoader
import Properties._

class Build {

	/////////////////////////////////////////////////////////////
	//
	// This section is just support methods for the
	// real build logic below
	//
	/////////////////////////////////////////////////////////////


	//copy one file to another.  Both must be files and not directories
	def copy(source:File, dest:File) = {
	  if( !dest.exists ){
	    dest.getParentFile.mkdirs
	    dest.createNewFile
	  }

	  val in = new BufferedInputStream(new FileInputStream(source))
	  val out = new BufferedOutputStream(new FileOutputStream(dest))

	  val buf = new Array[Byte](1024)
	  var len = in.read(buf)
	  while ( len > 0) {
	    out.write(buf, 0, len);
	    len = in.read(buf)
	  }
	  in.close();
	  out.close();
	}

	// copy all files in source directory to the dest directory
	// returns the new destination
	def copyDir(source:File, dest:File, fileFilter:(File)=>Boolean):File = {
	  dest.mkdirs();

	  var files = source.listFiles();

	  files.filter( fileFilter ).foreach ( f=> {
	    val newDest = new File( dest, f.getName() )
	    if( f.isDirectory() ){
	       copyDir( f, newDest, fileFilter )
	    } else {
	      copy( f, newDest )
	    }
	  })

	  dest
	}

	// deletes a directory and all subdirectories and files
	def deleteDir(path:File):Boolean = {
		if( path.isDirectory ) {
			path.listFiles.foreach ( deleteDir( _ ) )
		}

		path.delete
	}

	// Runs an ant script.  This is for running the PDE build and
	// others until they are converted to scala
	def runAntScript( buildfile:String ){
	  //look up required variable from system properties
	  // these will have been loaded from the properties file
	  val eclipseLocation = System.getProperty("eclipseLocation")
	  val equinoxLauncherPluginVersion = System.getProperty("equinoxLauncherPluginVersion")

	  // find the jar with the launcher Main class, create a Classloader for the
	  // jar
	  val jarFile = new File( eclipseLocation+"/plugins/org.eclipse.equinox.launcher_"+equinoxLauncherPluginVersion+".jar")
	  val classloader =  URLClassLoader.newInstance( Array( jarFile.toURI.toURL ) )
	  val mainClass = classloader.loadClass("org.eclipse.equinox.launcher.Main")

	  val mainMethod = mainClass.getMethod("main", Array(classOf[Array[String]]) )

	  // create the parameters and execute the PDE build
	  val mainArgs = Array("-noExit",
	        "-application",
	        "org.eclipse.ant.core.antRunner",
	        "-buildfile",
	        buildfile
	        )
	  mainMethod.invoke( mainClass, Array(mainArgs) )

		// this is to overcome a bug in the build system where zip is actually used but
     // the extendsion is written out as tar.gz (!buggers)
      def bugFileFilter(file:File) = {
		val result = file.getName.contains("macosx") && file.getName.endsWith(".tar.gz")
		result
	  }
      lookupArtifacts.filter( bugFileFilter _ ).foreach(
        file => {
			val to = new File(file.getPath.replaceAll(".tar.gz",".zip"))
			println ("Renaming "+file+" to "+to)
			file.renameTo( to )
		}
	   )

	}

    lazy val archiveDir:File = {
      val buildType = System.getProperty( "buildType" )
      val buildId = System.getProperty( "buildId" )
      new File(buildDirectory, buildType+"."+buildId)
    }

	// looks up all the artifacts created by the build
	def lookupArtifacts( ):Seq[File] = {

     def filter(f:java.io.File) = {
	    val filename = f.getName().toLowerCase()

	    f.isFile() && (filename.endsWith(".zip") ||
	      filename.endsWith( ".tar.gz" )||
	      filename.endsWith( ".exe" ) )
	  }

     archiveDir.exists match{
      case true => archiveDir.listFiles.filter( filter _ )
      case _ => Nil
     }
	}

	////////////////////////////////////////////////////////////////////
	//
	// Commonly used properties
	//
	////////////////////////////////////////////////////////////////////

	// directory where the build takes place
	def buildDirectory():File = new File(System.getProperty("buildDirectory"))
	// the location that all the plugins must be copied to in order for the
	// PDE build to pick them up
	def plugins():File = new File( buildDirectory, "plugins" )
	// the location that all the features must be copied to in order for the
	// PDE build to pick them up
	def features():File = new File( buildDirectory, "features" )
	// the location of the eclipse used for obtaining plugins for
	// building the application (the target)
	def eclipseLocation():File = new File(System.getProperty("eclipseLocation") )

	////////////////////////////////////////////////////////////////////
	//
	// Below is the script that actually does the work
	// You can think of the methods below as ant tasks
	//
	///////////////////////////////////////////////////////////////////

	// function that returns true if the File is not a compiled artifact
	// IE is not a .class file
	def sourceArtifacts (file:File):Boolean = {
	  !(file.getName().endsWith(".class") || file.getName().equals("bin") ||
	    file.getPath().contains("/bin/") || file.getPath().contains("\\bin\\"))
	}

	// copy files required for the application build to the build directory
	def init():Unit = {
	  buildDirectory.mkdirs
	  plugins.mkdirs
	  features.mkdirs

	  log("INFO", "Copying features" )
	  copyDir( new File("../features"), features, sourceArtifacts )
	  log("INFO", "Copying plugins" )
	  copyDir( new File("../plugins"), plugins, sourceArtifacts )
	  log("INFO", "Copying fragments" )
	  copyDir( new File("../fragments"), plugins, sourceArtifacts )

	  runAntScript(plugins+"/net.refractions.udig.libs/copy.xml" )
	}

	// perform the build
	def build():Unit = {

	  val pdeBuildPluginVersion = System.getProperty("pdeBuildPluginVersion")
	  val buildfile = eclipseLocation+"/plugins/org.eclipse.pde.build_"+pdeBuildPluginVersion+"/scripts/productBuild/productBuild.xml"

	  runAntScript(buildfile)
	}

    def packageApp(){
      import net.refractions.udig.build.pack._

      val packageSrc = new File(archiveDir.getParentFile, "prepackage")
      if( !packageSrc.exists ) {
        packageSrc.mkdir()
        copyDir(archiveDir, packageSrc, file=>true)
        lookupArtifacts.foreach( _.delete )
        val config = Configuration(System.getProperties)
        config.setOutputDir(archiveDir.getPath)
        config.setResources("../net.refractions.udig.build-package/")

        val filesForPackaging=packageSrc.listFiles().filter( _.getName.endsWith(".zip") )

        def filterNoMatch( pair:(Platform.Value, String)):Boolean = {
			val (platform,file)=pair
			file.contains("-"+platform.toString+".")
		}

        val inParams = filesForPackaging.flatMap {
          file => (for( platform <- Platform ) yield ( platform, file.getPath) ).toList
        }.filter ( filterNoMatch )

        println(inParams.mkString("\n"))

        config.setInput(Map(inParams:_*))

        pack.Main.execute(config, inParams.map(  _._1 ).toList )
      }
      log ("INFO", "Applications are packaged")
    }

    def timestamp()={
      import java.text.DateFormat._
	  val time = java.text.DateFormat.getDateTimeInstance(MEDIUM,MEDIUM).
          format(System.currentTimeMillis()).replaceAll("""[ /]""","").replaceAll(":",".")
	  log ("INFO", "Timestamp time = "+time)

	  lookupArtifacts().foreach( artifact => {
	    val timestamped = new File( artifact.getParentFile(), artifact.getName.replaceFirst("""\{timestamp\}""",time) )

        if( !artifact.equals(timestamped) && !artifact.renameTo(timestamped ) ){
          copy( artifact, timestamped )
          artifact.delete()
        }

      })
   }

	// timestamp all the artifacts created by build and copy them to the
	// directory indicated by DEPLOY_DIR
	def deployLocal():Unit = {
	  val deployDir =
	     if ( System.getenv("DEPLOY_DIR")!=null ){
	       new File(System.getenv("DEPLOY_DIR"))
	     } else {
	       new File(System.getProperty("DEPLOY_DIR"))
	     }

	  lookupArtifacts().foreach( artifact => {
        val destination = new File( deployDir, artifact.getName() )
        if( !destination.exists ){
  	      log( "INFO", "Copying "+artifact+" to "+destination )
	      copy( artifact, destination )
        }
	  }  )

	}

	// deletes the build directory
	def clean():Unit = {
	  log ("INFO", "Deleting the build directory: "+buildDirectory)
	  deleteDir( buildDirectory )
	}

}
