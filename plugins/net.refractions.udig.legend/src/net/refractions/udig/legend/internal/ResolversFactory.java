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
package net.refractions.udig.legend.internal;

import java.awt.Rectangle;
import java.io.IOException;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.legend.ui.LegendGraphic;
import net.refractions.udig.mapgraphic.internal.MapGraphicResource;
import net.refractions.udig.mapgraphic.style.LocationStyleContent;

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
