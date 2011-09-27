package eu.udig.catalog.csw

import ResultTypes._
import OutputSchemas._
import ElementSetNames._
import java.io.InputStream
import scala.xml.XML
import scala.xml.Node
import collection.JavaConverters._

class GetRecordsParams(
  val filter: OgcFilter,
  val resultType: ResultType = results,
  val startPosition: Int = 1,
  val maxRecords: Int = 50,
  val elementSetName: ElementSetName = full,
  val sortBy: Option[SortBy] = None) {
  def this(filter: OgcFilter) =
    this(filter, results)
  def this(filter: OgcFilter, maxRecords: Int) =
    this(filter, results, maxRecords = maxRecords)
  def this(filter: OgcFilter, startPosition: Int, maxRecords: Int) =
    this(filter, results, startPosition = startPosition, maxRecords = maxRecords)
}

abstract class GetRecords[A](basicParams: GetRecordsParams, outputSchema: OutputSchema) extends XmlRequest[A] {

  import basicParams._

  val xmlData = CswXmlUtil.getRecordsXml(filter.xml, resultType, outputSchema,
    startPosition, maxRecords, elementSetName, sortBy)

}

class GetDublinCoreRecords(basicParams: GetRecordsParams) extends GetRecords[BiSeq[DublinCore]](basicParams, Record) {
  val inProcessor = (in: InputStream) => {
    val xml = XML load in
    Logging.fine("GetRecords: " + xml)
    new BiSeq(xml \\ "Record" map (new DublinCore(_)))
  }
}

class DublinCore(xml: Node) {
  val UNKNOWN_TYPE = "Unknown"
  val WMS_TYPE = "WMS"

  lazy val description = (xml \\ "abstract").text.trim
  lazy val id = (xml \\ "identifier").text.trim
  lazy val title = (xml \\ "title").text.trim
  lazy val subjects = (xml \\ "subject").toSeq flatMap (_.text.trim.split(","))
  lazy val javaSubjects = subjects.asJava
  lazy val source = (xml \\ "source").text.trim
  lazy val language = (xml \\ "language").text.trim
  lazy val scalaDescriptions = (xml \\ "URI" map { new DcURI(_) })
  lazy val javaDescriptions = scalaDescriptions.asJava
  lazy val thumbnail = (xml \\ "URI" find (n => (n \\ "@name").text.trim.toLowerCase == "thumbnail")).map(_.text)

  lazy val `type`:String = scalaDescriptions.map(_.`type`).find(_ != UNKNOWN_TYPE) getOrElse UNKNOWN_TYPE
  lazy val onlineResource = scalaDescriptions.find(_.`type` != UNKNOWN_TYPE).map(_.value)
  lazy val bbox = (xml \\ "BoundingBox").headOption map { xml =>
    val Array(ux, uy) = (xml \\ "UpperCorner").text.split(' ').map(_.toDouble)
    val Array(lx, ly) = (xml \\ "LowerCorner").text.split(' ').map(_.toDouble)
    new {
      val west = ux min lx
      val east = ux max lx
      val north = uy max ly
      val south = uy min ly
      val epsgCode = (xml \\ "@crs").headOption.map(_.text.split(':').last)
    }
  }
  
  class DcURI(xml: Node) {
    lazy val `type` = protocal match {
      case x if (x startsWith "OGC:WMS") && (x contains "http-get-map") => WMS_TYPE
      case _ => UNKNOWN_TYPE
    }

    lazy val description = (xml \\ "@description").text
    lazy val name = (xml \\ "@name").text
    lazy val protocal = (xml \\ "@name").text
    lazy val value = xml.text
  }

}

