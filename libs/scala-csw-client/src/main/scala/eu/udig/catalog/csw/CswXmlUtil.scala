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

import ResultTypes._
import ElementSetNames._
import xml.{NodeSeq, Node}
import xml.Elem
import DomainProperties.DomainProperty
import DomainParameters.DomainParameter

object CswXmlUtil {
  def getByIdXml(fileId:String, resultType:ResultType, outputSchema:OutputSchemas.OutputSchema) =
    <csw:GetRecordById xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType={resultType.toString} outputSchema={outputSchema.toString}>
      <csw:Id>{fileId}</csw:Id>
    </csw:GetRecordById>

  def getRecordsXml(filter:NodeSeq=Nil,
                    resultType:ResultType=hits,
                    outputSchema:OutputSchemas.OutputSchema=OutputSchemas.IsoRecord,
                    startPosition:Int=1,
                    maxRecords:Int=50,
                    elementSetName:ElementSetName = full,
                    sortBy:Option[SortBy] = None) = {
    <csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType={resultType.toString}
                    startPosition={startPosition.toString} maxRecords={maxRecords.toString} outputSchema={outputSchema.toString}>
      <csw:Query typeNames="csw:Record">
        <csw:ElementSetName>{elementSetName}</csw:ElementSetName>
        {sortBy match {
          case None => Nil
          case Some(sortBy) =>
            <ogc:SortBy xmlns:ogc="http://www.opengis.net/ogc">
              <ogc:SortProperty>
                <ogc:PropertyName>{sortBy.key}</ogc:PropertyName>
                <ogc:SortOrder>{if(sortBy.asc)'A' else 'D'}</ogc:SortOrder>
              </ogc:SortProperty>
            </ogc:SortBy>
        }
      }
        {
        if(filter.nonEmpty){
          <csw:Constraint version="1.0.0">
            <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
              {filter}
            </ogc:Filter>
          </csw:Constraint>
        } else Nil
      }
      </csw:Query>
    </csw:GetRecords>
  }
}


