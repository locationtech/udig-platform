package eu.udig.catalog.csw
import scala.xml.NodeSeq

trait OgcFilter {
	def and(other:OgcFilter) = And(Seq(this, other))
	def or(other:OgcFilter) = Or(Seq(this, other))
	
	def xml:NodeSeq
}