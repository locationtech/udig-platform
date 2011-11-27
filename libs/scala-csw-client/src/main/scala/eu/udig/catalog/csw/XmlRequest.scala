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