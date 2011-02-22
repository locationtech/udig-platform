package net.refractions.udig.build.app
import java.io._
import Logging._

class SdkBuild extends Build{


	//copy files required for the sdk build to the build directory
	override def init():Unit = {
	 super.init()
	 buildDirectory.mkdirs
	 plugins.mkdirs
	 features.mkdirs

	 log("INFO", "Copying tutorials" )
	 copyDir( new File("../tutorials"), plugins, sourceArtifacts )

	  runAntScript(plugins+"/net.refractions.udig.libs.source/jars.xml" )
	}


	def sdkPostBuild():Unit = {
	  log ("INFO", "Adding Delta pack to SDK")
	  val jarNames = System.getProperty("deltaPackPlugins").split(",").map( _.trim() )

	  // matches patterns in the property to files in the  plugins and features directories
	  var deltaPackFiles = jarNames.flatMap( pattern => {
	    val path = new File( eclipseLocation, pattern.substring( 0, pattern.lastIndexOf( "/" ) ) )
	    val nameFragment =
	      if( pattern.endsWith( "*" ) ){
	        pattern.substring( pattern.lastIndexOf("/")+1, pattern.lastIndexOf("*") )
	      } else {
	        pattern.substring( pattern.lastIndexOf("/")+1 )
	      }

	    path.listFiles().filter( file => {
	      	if( pattern.endsWith("*") ) {
	           file.getName().startsWith(nameFragment)
	         } else {
	           file.getName().equals(nameFragment)
	         }
	     } )
	  } )

	  // returns all the files with in the directory or a singleton with the file if the file
	  // is not a directory
	  def expand (file:File):Iterable[File]={
	    if( file.isDirectory ){
	      file.listFiles().flatMap( expand )
	    } else {
	      Array( file )
	    }
	  }
	  // now expand files that are directories so we only have files
	  deltaPackFiles = deltaPackFiles.flatMap( expand )

	  println ( deltaPackFiles.mkString(",\n") )

	  for( artifact <- lookupArtifacts() ){
		updateZip ( artifact, deltaPackFiles, System.getProperty("archivePrefix"), eclipseLocation )
	  }

	}


	import java.util.zip._

	// updates a zip file by adding the provided file if it does not already exist
	// destZipDirectory is the directory in the zip to use as the base directory
	// relativePath is the portion of the path in the file to ignore when creating the ZipEntry in the zipfile
	def updateZip( zipFile:File,files:Seq[File], destZipDirectory:String, relativePath:File):Unit = {

	  if( files.length == 0 ){
	    return
	  }

	  //get a temp file
	  val tempFile = new File(zipFile.getParentFile(), "tmpZip"+System.currentTimeMillis()+".zip")
	  // delete it, otherwise you cannot rename your existing zip to it.
	  tempFile.delete()

	  val renameOk=zipFile.renameTo(tempFile)
	  if (!renameOk)
	  {
	        throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath())
	  }

	  tempFile.deleteOnExit()

	  val buf = new Array[Byte](1024)

	  val zin = new ZipInputStream(new FileInputStream(tempFile))
	  val out = new ZipOutputStream(new FileOutputStream(zipFile))

	  var entry = zin.getNextEntry()

	  def copyData (zin:InputStream, out:OutputStream):Unit= {
	    var len = zin.read(buf)
	    while (len > 0) {
	            out.write(buf, 0, len)
	            len = zin.read(buf)
	    }
	  }

	  val keyNames = new scala.collection.mutable.ListBuffer[String]()

	  while (entry != null) {
	        val name = entry.getName()
	        keyNames += name
	        // Add ZIP entry to output stream.
	        out.putNextEntry(new ZipEntry(name))
	        // Transfer bytes from the ZIP file to the output file
	        copyData( zin, out )
	        entry = zin.getNextEntry()
	  }
	  // Close the streams
	  zin.close()

	  // Add new files
	  for (file <- files) {
	    var newName = file.getPath().drop(relativePath.getPath().length)
	    if( !newName.startsWith( "/" ) ) newName = "/"+newName.drop(1)
	    newName = destZipDirectory+newName

	    if ( keyNames.find( name=> {newName.toString.equals(name.toString)} ) == None ){
		keyNames += newName

	        val in = new FileInputStream(file)
	        // Add ZIP entry to output stream.
	        val newEntry = new ZipEntry(newName)


	       log( "INFO", "Adding: "+newName+" to archive: "+zipFile )

	        out.putNextEntry(newEntry)
	        // Transfer bytes from the file to the ZIP file
	        copyData( in, out )
	        // Complete the entry
	        out.closeEntry()
	        in.close()
	    }
	  }
	  // Complete the ZIP file
	  out.close()
	  tempFile.delete()

	}

}
