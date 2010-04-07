package net.refractions.udig.build.pack

import java.io.File
import java.util.Properties
import scala.maven.model.MavenProjectAdapter
import org.apache.maven.plugin.logging.Log

object Package {
  
  def apply(confFile:File,  project:MavenProjectAdapter, log:Log){
	val configuration = resolveProperties(confFile)

    val buildFiles = new File(configuration("buildDirectory")+"/"+configuration("buildLabel")).listFiles.filter( file=> file.isFile )
    
	val timestamp = new java.text.SimpleDateFormat("yyyyMMddHH").format(new java.util.Date() )
    val param = new Params(configuration, project, log, timestamp)
    
	for( file <- buildFiles ) {
		log.info("Starting to package "+file)
		file.getName match {
			case Linux32( x,y,z ) => new LinuxPrepare(param.copy(file, Linux32)).run()
			case Linux64(x,y,z) => new LinuxPrepare(param.copy(file, Linux64)).run()
			case Win32(x,y,z) => new WindowsPrepare(param.copy(file, Win32)).run()
			case MacPPC(x,y,z) => new MacPrepare(param.copy(file, MacPPC)).run()
			case Mac86(x,y,z) => new MacPrepare(param.copy(file, Mac86)).run()
			case _ => null // ignore
		}
	  }
	  

				
	}
	

  def resolveProperties(confFile:File):Map[String,String] = {
	
	def resolve(props:Map[String,String]):Map[String,String] = {
		
		val unresolved = props.filter( e => e._2.matches(""".*\$\{(\S+[^\$\{\}])\}.*""") )
		if( unresolved.isEmpty ){
			props
		} else {
			def lookup(key:String)={
				if ( System.getProperty(key) !=null ){
					System.getProperty(key)
				}else if (System.getenv(key)!=null){
					System.getenv(key)
				}else{
					props(key)
				}
			}
			val partialResolved = 
				for( (key,value) <- unresolved ) yield {
				    val start = value.indexOf("${")
					val ref = value.substring( start+2, value.indexOf("}", start) )
					val updated = value.take(start)+lookup(ref)+value.drop(value.indexOf( "}",start)+1)
					(key,updated)
				}
				resolve( props ++ partialResolved  )
			}
		
		}
		
		resolve(loadProperties(confFile))
    }

	def loadProperties(confFile:File) = {
			val props = new Properties()
			val in = new java.io.FileInputStream(confFile)
			try{
				props.load(in)
			}finally{
				in.close()
			}
			val e = props.entrySet.iterator

			val converter = new Iterator[(String,String)]{
				def hasNext = e.hasNext
				def next = {
					val n = e.next
					(n.getKey.asInstanceOf[String], n.getValue.asInstanceOf[String])
				}
			}

		    Map(converter.toList.toArray:_*)
	}
}


trait Platform {
	val id:String
	def unapply(string:String):Option[(String,String,String)]
}
object Linux32 extends Platform {
	val id = "linux.gtk.x86"
	def unapply(str: String): Option[(String,String,String)] = {
		if( str.toLowerCase.contains(id) && Linux64.unapply(str).isEmpty ) {
			val parts = str.split(id)
			 new Some(parts(0),id,parts(1))
		} else None
	}
}

object Linux64 extends Platform {
	val id = "linux.gtk.x86_64"
	def unapply(str: String): Option[(String,String,String)] = {
		if( str.toLowerCase.contains(id) ) {
			val parts = str.split(id)
			 new Some(parts(0),id,parts(1))
		} else None
	}
}
object Win32 extends Platform {
	val id = "win32.win32.x86"
	def unapply(str: String): Option[(String,String,String)] = {
		if( str.toLowerCase.contains(id) ) {
			val parts = str.split(id)
			 new Some(parts(0),id,parts(1))
		} else None
	}
}
object MacPPC extends Platform {
	val id = "macosx.carbon.ppc"
	def unapply(str: String): Option[(String,String,String)]  ={
		if( str.toLowerCase.contains(id) ) {
			val parts = str.split(id)
			 new Some(parts(0),id,parts(1))
		} else None
	}
}
object Mac86 extends Platform {
	val id = "macosx.carbon.x86"
	def unapply(str: String): Option[(String,String,String)] = {
		if( str.toLowerCase.contains(id) ) {
			val parts = str.split(id)
			 new Some(parts(0),id,parts(1))
		} else None
	}
}


