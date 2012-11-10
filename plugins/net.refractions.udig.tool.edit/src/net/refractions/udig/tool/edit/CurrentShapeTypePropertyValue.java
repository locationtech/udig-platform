/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tool.edit;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

/**
 * Returns true if the current editGeom, see {@link net.refractions.udig.tools.edit.EditToolHandler#getCurrentGeom()}
 * matches the type of the value passed.  Legal values are POLYGON, LINE, POINT, UNKNOWN
 * @author Jesse
 * @since 1.1.0
 */
public class CurrentShapeTypePropertyValue implements PropertyValue {

    public boolean isTrue( Object object, String value ) {
        IMap map = ApplicationGIS.getActiveMap();
        if( map==ApplicationGIS.NO_MAP || map.getMapLayers().size()==0 )
            return false;
        PrimitiveShape shape = (PrimitiveShape) map.getBlackboard().get(EditToolHandler.CURRENT_SHAPE);
        if( shape==null )
            return false;
        
        return shape.getEditGeom()!=null && shape.getEditGeom().getShapeType().toString().equalsIgnoreCase(value.trim());
    }

    public void addListener( IOpFilterListener listener ) {
        // do nothing
    }

    public boolean canCacheResult() {
        return true;
    }

    public boolean isBlocking() {
        return false;
    }

    public void removeListener( IOpFilterListener listener ) {
        // do nothing
    }

}
