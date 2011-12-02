package eu.udig.catalog.csw
import java.net.URL

class Context(val baseURL:String) {
  def timeout = 10000
}

case class GeonetworkContext(host:String,port:Int=80, lang:String="en") 
	extends Context("http://"+host+":"+port+"/geonetwork/srv/"+lang+"/csw")