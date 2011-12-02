package eu.udig.catalog.csw
import java.util.logging.Logger
import java.util.logging.Level

object Logging {
	def logger = Logger.getLogger("eu.udig.catalog.csw")
	def info(msg: => String) = {
	  if(logger.isLoggable(Level.INFO)) logger.info(msg)
	}
	def fine(msg: => String) = {
		if(logger.isLoggable(Level.INFO)) logger.info(msg)
	}
}