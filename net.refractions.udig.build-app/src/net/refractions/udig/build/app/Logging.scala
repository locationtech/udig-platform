package net.refractions.udig.build.app

object Logging {

	def log(severity:String, message:String) = {
	  println (severity+": "+message)
	}

}
