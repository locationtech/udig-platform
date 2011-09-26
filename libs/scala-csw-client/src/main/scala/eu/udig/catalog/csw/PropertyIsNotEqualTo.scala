package eu.udig.catalog.csw

case class PropertyIsNotEqualTo(name:String,literal:String) extends OgcFilter {
  val xml =
   <ogc:PropertyIsNotEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsNotEqualTo>
}
