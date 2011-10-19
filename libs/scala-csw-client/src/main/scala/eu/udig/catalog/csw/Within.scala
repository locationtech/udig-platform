package eu.udig.catalog.csw

case class Within(areacode:String) extends OgcFilter{
	def xml = <ogc:Within><ogc:PropertyName>ows:BoundingBox</ogc:PropertyName><gml:MultiPolygon xmlns:gml="http://www.opengis.net/gml" gml:id={areacode}/></ogc:Within>
}
case class Contains(areacode:String) extends OgcFilter{
    def xml = <ogc:Contains><ogc:PropertyName>ows:BoundingBox</ogc:PropertyName><gml:MultiPolygon xmlns:gml="http://www.opengis.net/gml" gml:id={areacode}/></ogc:Contains>
}
