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

import xml.{Node,XML}

abstract class XmlRequest[R] extends Request[R] {
  final val cswRequestName = ""
  final override val params = Seq.empty[(String,String)]
  override final def createUrlString(context:Context) = context.baseURL
  
  def xmlData:Node
  
  def execute(context:Context) = {
    val connection = openConnection(context)
    connection.addRequestProperty("Content-Type", "application/xml;charset=utf-8")
    connection.setDoOutput(true)
    connection.getOutputStream().write(xmlData.toString.getBytes("UTF-8"))
    val inputStream = connection.getInputStream()
    inProcessor(inputStream)
  }
}