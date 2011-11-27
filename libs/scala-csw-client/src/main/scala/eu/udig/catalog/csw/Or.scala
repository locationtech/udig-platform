package eu.udig.catalog.csw

case class Or(filters: Seq[OgcFilter]) extends OgcFilter {
  def this(filter1: OgcFilter, filter2: OgcFilter) = this(Seq(filter1, filter2))

  def xml = <ogc:Or>{ filters.map(_.xml) }</ogc:Or>
  
  override def or(other: OgcFilter) = other match {
    case otherOr: Or => Or(filters ++ otherOr.filters)
    case other => Or(filters :+ other)
  }
  override def toString = filters mkString " or "

}