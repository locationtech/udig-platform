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

case class Or(filters: Seq[OgcFilter]) extends OgcFilter {
  def this(filter1: OgcFilter, filter2: OgcFilter) = this(Seq(filter1, filter2))

  def xml = <ogc:Or>{ filters.map(_.xml) }</ogc:Or>
  
  override def or(other: OgcFilter) = other match {
    case otherOr: Or => Or(filters ++ otherOr.filters)
    case other => Or(filters :+ other)
  }
  override def toString = filters mkString " or "

}