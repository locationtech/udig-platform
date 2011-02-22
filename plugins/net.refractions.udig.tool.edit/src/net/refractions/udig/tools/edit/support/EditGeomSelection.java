/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.support;

/**
 * A selection that IS a EditGeom.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class EditGeomSelection extends Selection {

    private EditGeom geom;

    public EditGeomSelection( EditGeom geom ) {
        super(geom.getEditBlackboard() );
        this.geom=geom;
        for( PrimitiveShape shape : geom ) {
            for( Point point : shape ) {
                add(point, shape.getMutator().getLazyCoordsAt(point));
            }
        }
    }

    public EditGeom getGeom() {
        return geom;
    }

}
