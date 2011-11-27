package eu.udig.catalog.csw;
import java.io.InputStream
import scala.xml.XML
import scala.xml.NodeSeq

case class GetDomain(firstParam: DomainParam, domainParams: DomainParam*) extends XmlRequest[Seq[String]] {
	val inProcessor = (in:InputStream) => {
	  val xml = XML.load(in)
	  Logging.fine("GetDomain: "+xml)
	  val values = xml \\ "Value"
	  values map (_.text.trim)
	}
	val (propertyNames, parameterNames) = {
    val (propertyNames, parameterNames) = domainParams.partition(_.isInstanceOf[DomainProperties.DomainProperty])
    firstParam match {
      case p:DomainParameters.DomainParameter =>
        (propertyNames, p +: parameterNames)
      case p:DomainProperties.DomainProperty =>
        (p +: propertyNames, parameterNames)
    }
  }

  def xmlData =
    <csw:GetDomain xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW">
      {
        if(propertyNames.nonEmpty) {
          <csw:PropertyName>{propertyNames.map{_.name}.mkString(",")}</csw:PropertyName>
        } else {
          NodeSeq.Empty
        }
      }
      {
        if(parameterNames.nonEmpty) {
          <csw:ParameterName>{parameterNames.map{_.name}.mkString(",")}</csw:ParameterName>
        } else {
          NodeSeq.Empty
        }
      }
    </csw:GetDomain>
}

trait DomainParam { val name: String }
object DomainProperties {
  abstract class DomainProperty(val name: String) extends DomainParam
  case object Title extends DomainProperty("Title")
  case object Subject extends DomainProperty("Subject")
  case object Owner extends DomainProperty("_owner")
  case object TopicCategory extends DomainProperty("topicCat")
  case object Type extends DomainProperty("Type")
  case object Keyword extends DomainProperty("Keyword")

}

object DomainParameters {
  abstract class DomainParameter(val name: String) extends DomainParam
  case object DescribeRecordOutputFormat extends DomainParameter("DescribeRecord.outputFormat")
  case object GetRecordsResultType extends DomainParameter("GetRecords.resultType")
}