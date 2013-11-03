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

case class PropertyIsEqualTo(name:String,literal:String) extends OgcFilter {
  val xml =
   <ogc:PropertyIsEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsEqualTo>
}
