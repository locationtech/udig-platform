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
import scala.xml.{ Node, XML }
import java.io.InputStream

object GetCapabilities extends Request[Capabilities] {
  val inProcessor = (in: InputStream) => new Capabilities(XML.load(in))

  val cswRequestName = "GetCapabilities"

  def execute(context: Context) = {
    val connection = openConnection(context)
    inProcessor(connection.getInputStream)
  }

}

class Capabilities(val xml: Node) {
  self =>
    
    Logging.fine("Capabilities: "+xml)

  lazy val title = (xml \\ "Title").text.trim
  lazy val `abstract` = (xml \\ "Abstract").text.trim
  lazy val keywords = (xml \\ "Keywords" \ "Keyword") map (_.text.trim)
  lazy val getRecords = GetRecords(xml).get

  object Operation {
    def findOp(xml: Node, name: String) = {
      xml \\ "Operation" find { n => (n \ "@name").text.trim == name }
    }
    def findValues(xml: Node, nodeName: String, name: String): List[String] = {
      val param = xml \\ nodeName find { n => (n \ "@name").text.trim.toLowerCase == name.toLowerCase }
      param.toList flatMap { _ \\ "Value" map (_.text.trim) }
    }
    def findParam(xml: Node, name: String) = findValues(xml, "Parameter", name)
    def findConstraint(xml: Node, name: String) = findValues(xml, "Constraint", name)
  }

  import Operation._
  object GetRecords {
    def apply(xml: Node) = findOp(xml, "GetRecords").map(new GetRecords(_))
  }

  class GetRecords(xml: Node) {
    lazy val resultTypes = findParam(xml, "resultType")
    lazy val outputFormats = findParam(xml, "outputFormat")
    lazy val outputSchemas = findParam(xml, "outputSchema")
    lazy val typeNames = findParam(xml, "typeName")
    lazy val constraintLanguages = findParam(xml, "CONSTRAINTLANGUAGE").map(_.toUpperCase)
    lazy val postEncodings = findConstraint(xml, "PostEncoding").map(_.toUpperCase)
    lazy val queryables = findConstraint(xml, "SupportedISOQueryables") ++
      findConstraint(xml, "AdditionalQueryables")

  }
}
