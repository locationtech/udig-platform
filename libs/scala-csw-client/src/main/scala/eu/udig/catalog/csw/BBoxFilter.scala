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

class BBoxFilter(minx:Double, miny:Double, maxx:Double, maxy:Double) extends OgcFilter {
  def xml = 
    <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
		  <ogc:BBOX>
		  	<ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>
		  	<gml:Envelope xmlns:gml="http://www.opengis.net/gml">
		  		<gml:lowerCorner>{minx} {miny}</gml:lowerCorner>
		  		<gml:upperCorner>{maxx} {maxy}</gml:upperCorner>
		  	</gml:Envelope>
		  </ogc:BBOX>
      </ogc:Filter>
}