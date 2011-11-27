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