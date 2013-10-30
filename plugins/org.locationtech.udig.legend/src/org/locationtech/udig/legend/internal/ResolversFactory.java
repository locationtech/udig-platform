/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.legend.internal;

import java.awt.Rectangle;
import java.io.IOException;

import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.legend.ui.LegendGraphic;
import org.locationtech.udig.mapgraphic.internal.MapGraphicResource;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Resolved LegendGraphic Resources
 * @author jesse
 * @since 1.1.0
 */
public class ResolversFactory implements IResolveAdapterFactory {

    public <T> T adapt( IResolve resolve, Class<T> adapter,
            IProgressMonitor monitor ) throws IOException {
        MapGraphicResource resource = (MapGraphicResource) resolve;
        if( canResolveToRectangle(adapter, resource)){
            // this is the null style.  It says to place it in the 
            // bottom left at the optimal size
            Rectangle rectangle = new Rectangle(-LocationStyleContent.XPAD_RIGHT,-LocationStyleContent.YPAD_BOTTOM,0,0);
            return adapter.cast(rectangle);
        }
        return null;
    }

    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {
        MapGraphicResource resource = (MapGraphicResource) resolve;
        
        return canResolveToRectangle(adapter, resource);
    }

    private boolean canResolveToRectangle( Class< ? extends Object> adapter,
            MapGraphicResource resource ) {
        if( resource.getMapGraphic() instanceof LegendGraphic ){
            if( adapter.isAssignableFrom(Rectangle.class) ){
                return true;
            }
        }
        
        return false;
    }

}
