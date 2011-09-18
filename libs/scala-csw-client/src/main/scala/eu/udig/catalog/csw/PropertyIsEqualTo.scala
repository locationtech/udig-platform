package eu.udig.catalog.csw

case class PropertyIsEqualTo(name:String,literal:String) extends OgcFilter {
  val xml =
   <ogc:PropertyIsEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsEqualTo>
}
