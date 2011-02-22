package net.refractions.udig.build.pack

import net.pragmaticdesign.scala.io._

class Configuration(configuration:java.util.Properties) {

  def unapply(prop:String,value:String){
    configuration.setProperty(prop,value)
  }

  def setOutputDir(value:String){
    configuration.setProperty("package-out", value)
  }

  def setInput(params:Map[Platform.Value,String]){
    params.foreach( param =>{
      configuration.setProperty("in-"+param._1, param._2)
    })
  }

  def setResources(value:String){
    configuration.setProperty("package-resources",value)
  }
   lazy val wine = configuration.getProperty("wine")
   lazy val makensis = configuration.getProperty("makensis")
   lazy val version = configuration.getProperty("version")
   lazy val resources = configuration.getProperty("package-resources")
   def buildDirectory():File={
     File(configuration.getProperty("buildDirectory"))
   }
   def out(platform:Platform.Value):File={
     File(configuration.getProperty("package-out")+"/"+platform)
   }
   def in(platform:Platform.Value):File={
     val baseFile = configuration.getProperty("in-"+platform)
     baseFile match {
     case _ if( baseFile.endsWith(".zip")
            || baseFile.endsWith(".tar.gz")
     	    || baseFile.endsWith(".tar" ))   => File(baseFile+"#udig")
     case _ => File(baseFile+"/udig")
     }
   }
}

  object Platform extends Enumeration{
    val MAC_CARBON_PPC=Value("macosx.carbon.ppc")
    val MAC_CARBON_X86= Value("macosx.carbon.x86")
    val WIN32_X86= Value("win32.win32.x86")
    val LINUX_GTK_X86= Value("linux.gtk.x86")
    val LINUX_GTK_X86_64= Value("linux.gtk.x86_64")
  }

object Configuration{
  def apply(configuration:java.util.Properties) = new Configuration(configuration)
  def apply(configurationFile:String):Configuration = {
   val configuration = new java.util.Properties()
   configuration.load(new java.io.FileInputStream(configurationFile))
   new Configuration(configuration)
  }
}
