package eu.udig.catalog.csw

case class And(val filters:Seq[OgcFilter]) extends OgcFilter {
  def this(filter1: OgcFilter, filter2: OgcFilter) = this(Seq(filter1, filter2))
	
  def xml = <ogc:And>{filters.map(_.xml)}</ogc:And>
  
	override def and(other:OgcFilter) = other match {
	  case otherAnd:And => And(filters ++ otherAnd.filters)
	  case other => And(filters :+ other)
	}
	override def toString = filters mkString " and "
}