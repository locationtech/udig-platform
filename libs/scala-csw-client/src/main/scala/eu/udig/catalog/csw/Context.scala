/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Jesse Eichar
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package eu.udig.catalog.csw
import java.net.URL

class Context(val baseURL:String) {
  def timeout = 10000
}

case class GeonetworkContext(host:String,port:Int=80, lang:String="en") 
	extends Context("http://"+host+":"+port+"/geonetwork/srv/"+lang+"/csw")