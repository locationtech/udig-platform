package net.refractions.udig.build.app
import Logging._
import Properties._
import java.io.File

object Main {
  val appBuilder = new Build
  val sdkBuilder = new SdkBuild

  def main(args : Array[String]) : Unit = {
    parseTargets( args ).foreach( target => {
      println
      log( "INFO", "Beginning target: "+ target._3 )
      initProperties( target._2 )
      target._1.foreach( _.apply() )
	} )

	log ( "INFO", "BUILD COMPLETE" )
	println

	// this is required because I ran eclipse in noExit mod
	// so those threads will keep the vm running.  This is probably a hack
	// til I have a better way to call ant
    System.exit(0)
  }


	/////////////////////////////////////////////////////////////
	//
	//
	//
	//      Usage of script:

	def usage():Unit = {

	  println  ("scala build.scala [target]...")
	  println  ("  or if on unix:")
	  println  ("./build.scala [target]...")
	  println
	  println  ("options for target are:" )
	  println  ("    clean-app, clean-sdk, build-app, build-sdk,")
	  println  ("    deploy-local-app, deploy-local-sdk")
	  println
	  println ("If not parameters are selected then clean-app build-app and deploy-local-app targets are used")
	  println
	  println  ("example:  scala build.scala clean-app build-app deploy-local-app")
	  println  ("          This will clean then build then deploy the application to a local directory")
	  println  ("          as specified by deployDir in the build-app.properties")
	  println
	  println  ("clean-app - it will delete the build directory as read from build-app.properties")
	  println  ("clean-sdk - Same as clean-app but the properties found in build-sdk.properties will")
	  println  ("            override those found in build-app.properties")
	  println  ("build-app - This script will read the build-app.properties file which")
	  println  ("            is a modified version of that founds in the templates directory")
	  println  ("            of the PDE Build plugin.  Then it will copy the files required for building the")
	  println  ("            application to the build directory specified in the build-app.properties.")
	  println  ("            Finally it will use PDE build to build the application")
	  println  ("build-sdk - The same as build-app except the properties found in build-sdk.properties" )
	  println  ("            will override those found in build-app.properties")
	  println  ("deploy-local-app - Will timestamp the built artifacts and copy then to the")
	  println  ("                   directory indicated by the deployDir in the build-app.properties")
	  println  ("deploy-local-sdk - Same as deploy-local-app but the properties found in build-sdk.properties" )
	  println  ("                   will override those found in build-app.properties")
	}


	/**
     * Takes the commandline arguments and converts then into a list
	 * of methods to execute.  The second part of the Tuple is
	 * the properties file to use
     */
	def parseTargets( args:Array[String] ):Array[(List[()=>Unit], List[File], String)] = {
	  val appProperties = List(new File( "build-app.properties" ))
	  val sdkProperties = List(new File( "build-sdk.properties" ))

	  val checkedArgs =
	  if( args.length == 0) {
	         Array("clean-app", "build-app", "package-app", "timestamp-app","deploy-local-app")
	  } else {
	   args
	  }


	  val targets = for( arg <- checkedArgs ) yield {
	    arg match {
	     case "init-app" =>  ( List(appBuilder.init _), appProperties, arg )
	     case "init-sdk" =>  ( List( sdkBuilder.init _), appProperties:::sdkProperties, arg )
	     case "clean-app" =>  ( List(appBuilder.clean _), appProperties, arg )
	     case "clean-sdk" =>  ( List(sdkBuilder.clean _), appProperties:::sdkProperties, arg )
	     case "timestamp-app" =>  ( List(appBuilder.timestamp _), appProperties, arg )
	     case "timestamp-sdk" =>  ( List(sdkBuilder.timestamp _), appProperties:::sdkProperties, arg )
	     case "build-app" =>  ( List(appBuilder.init _, appBuilder.build _), appProperties, arg )
	     case "build-sdk" =>  ( List(sdkBuilder.init _, sdkBuilder.build _, sdkBuilder.sdkPostBuild _), appProperties:::sdkProperties, arg )
         case "package-app" => (List(appBuilder.packageApp _), appProperties, arg)
	     case "sdk-post-build" =>  ( List(sdkBuilder.sdkPostBuild _), appProperties:::sdkProperties, arg )
	     case "deploy-local-app" =>  ( List(appBuilder.packageApp _, appBuilder.timestamp _, appBuilder.deployLocal _), appProperties, arg )
	     case "deploy-local-sdk" =>  ( List(sdkBuilder.timestamp _, sdkBuilder.deployLocal _), appProperties:::sdkProperties, arg )
	     case "fullbuild-app" =>  ( List(appBuilder.clean _, appBuilder.init _, appBuilder.build _, appBuilder.packageApp _, appBuilder.timestamp _, appBuilder.deployLocal _), appProperties, arg )
	     case "fullbuild-sdk" =>  ( List(sdkBuilder.clean _, sdkBuilder.init _, sdkBuilder.build _, sdkBuilder.sdkPostBuild _, sdkBuilder.timestamp _, sdkBuilder.deployLocal _), appProperties:::sdkProperties, arg )
	     case _ => (null, null, null)
	    }
	  }

	  if( targets.exists( _._1 == null ) ) {
	    Array( ( List(usage _), appProperties, "usage" ) )
	  } else {
	    targets
	  }
	}

}
