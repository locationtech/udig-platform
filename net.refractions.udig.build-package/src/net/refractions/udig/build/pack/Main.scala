package net.refractions.udig.build.pack

import net.pragmaticdesign.scala.io._
import net.pragmaticdesign.scala.io.File._
import Platform._

object Main {
 def main(args:Array[String]):Unit = {

   val output = new BasicFile("output")
   output.deleteRecursive()

   val configurationFile = if( args.length==1) args (0) else "configuration.properties"

   val configuration = Configuration(configurationFile)
   configuration.setOutputDir("output")
   configuration.setResources("./")

   def toEntry(platform:Value,ext:String):(Value,String) = {
     (platform,"export/udig-"+configuration.version+"-"+platform+ext)
   }
   val input = Map(toEntry(MAC_CARBON_PPC,".zip"),
                   toEntry(MAC_CARBON_X86, ".zip"),
                   toEntry(WIN32_X86,".zip"),
                   toEntry(LINUX_GTK_X86,".zip"),
                   toEntry(LINUX_GTK_X86_64,".zip"))
   configuration.setInput(input)

    execute(configuration, List(LINUX_GTK_X86_64))
  }

  def execute(configuration:Configuration, platforms:List[Value]){

    for( platform <- platforms ){
      platform match {
        case _ if( platform.toString.contains("macosx") )=> new MacPrepare(platform, configuration ).createBundle()
        case _ if( platform.toString.contains("win") )=> new WindowsPrepare(platform, configuration ).createExe()
        case _ => new LinuxPrepare(platform, configuration ).createZip()
      }
    }
  }
}
