package eu.udig.catalog.csw

import org.junit.Test
import org.junit.Assert._

class BasicApiTest {

  @Test
  def getCapabilities {
    val capabilities = GetCapabilities.execute(Setup.context)

    assertTrue(capabilities.keywords.nonEmpty)

    assertTrue(capabilities.getRecords.constraintLanguages contains "FILTER")
    assertTrue(capabilities.getRecords.constraintLanguages contains "CQL_TEXT")
    assertTrue(capabilities.getRecords.postEncodings contains "XML")
    assertTrue(capabilities.getRecords.outputFormats.nonEmpty)
    assertTrue(capabilities.getRecords.outputSchemas.nonEmpty)
    assertTrue(capabilities.getRecords.queryables.nonEmpty)
  }

  @Test
  def getDomain {
    val domain = new GetDomain(DomainProperties.Type).execute(Setup.context)

    assertTrue(domain.nonEmpty)
  }

  @Test
  def getGetRecords {
    import ResultTypes._
    val params = new GetRecordsParams(AcceptAll,results, maxRecords = 100)
    
    val records = new GetDublinCoreRecords(params).execute(Setup.context).seq
 
    assertEquals(2, records.size)
    assertTrue(records.head.description.nonEmpty)
    assertTrue(records.head.title.nonEmpty)
    assertTrue(records.head.subjects.nonEmpty)
    assertTrue(records.head.bbox.nonEmpty)
  }

}