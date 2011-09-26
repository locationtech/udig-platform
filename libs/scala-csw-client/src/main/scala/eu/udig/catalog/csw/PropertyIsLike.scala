package eu.udig.catalog.csw

case class PropertyIsLike(name:String,literal:String) extends OgcFilter {
  val xml =
   <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!">
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsLike>
}