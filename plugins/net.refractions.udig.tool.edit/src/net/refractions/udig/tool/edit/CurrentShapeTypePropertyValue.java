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
