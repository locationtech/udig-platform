/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

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
