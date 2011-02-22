package net.refractions.udig.build.app

import java.io.File
import Logging._

object Properties {

	// Reads the build.properties file and sets them as System properties
	def initProperties( properties:List[File]) = {

	  val keys = properties.flatMap( file => readProperties (file) )

	  // now replace all ${...} with the correct value
	  def replaceVariables:Boolean = {
	    val replacePattern = java.util.regex.Pattern.compile(".*\\$\\{([^\\}]+)\\}.*" )
	    def needSubstitution( key:String ):Boolean = { System.getProperty(key).contains("${") }
	    val result = for( key <- keys; if needSubstitution(key) ) yield {
	            var property = System.getProperty(key)
	            val matcher = replacePattern.matcher( property )

	            if( matcher.matches ){
	              val substitute = System.getProperty( matcher.group(1) )
	              property = property.replace( "${"+matcher.group(1)+"}", substitute )
	              System.setProperty( key, property )
	            }
	             needSubstitution(key)
	      }
	    result.size > 0
	  }

	  while ( replaceVariables )
	  null
	}

	def readProperties( propertiesFile:File ):Iterable[String] = {
	  log( "INFO", "Loading properties from "+propertiesFile)
	  var propString = scala.io.Source.fromFile( propertiesFile ).getLines
	  propString = propString.map( line =>line.trim )

	  propString = propString.filter( line => !line.startsWith("#") && line.length()>0 )

	  // remove the last \ of the end of the string and any white space between
	  // that character and the last non-whitespace character
	  def trim( string:String ):String={
	    string.substring(0,string.length-1).trim()
	  }

	  // split the string into a tuple that is (key, value)
	  def split( line:String ): (String,String) = {
	    val parts = line.split("=")
	    val key = parts(0)
	    var value = ""
	    if( parts.length==2 ){
	      value = parts(1)
	    }

	    if( value.endsWith( "\\" ) ) {
	      ( key, trim(value) )
	    } else {
	      ( key, value)
	    }
	  }

	  // concatenate the two strings.  Remove the trailing \ if necessary
	  def concat (part1:String, part2:String) = {
	    if( part2.endsWith("\\") ){
	      part1+trim( part2 )
	    }else{
	      part1+part2
	    }
	  }


	  var keys:List[String] = Nil

	  // Set the system property
	  def systemProperty( property:(String,String) ): (String, String) = {
	    System.setProperty( property._1.trim(), property._2.trim() )
	    keys = property._1.trim()::keys
	    property
	  }

	  var property = ("","")
	  propString.foreach( line=> {
	      property = line match {
	        case s:String if s.endsWith("\\")
	        		 && s.contains("=")  =>  split(s.trim())
	        case s:String if s.endsWith("\\")    =>  (property._1.trim() , concat(property._2.trim(), s) )
	        case s:String if s.contains("=")     =>  systemProperty(split(s))
	        case s:String                        =>  systemProperty((property._1 , concat(property._2.trim(), s.trim()) ))
	      }

	    }
	  )

	  keys

	}


}
