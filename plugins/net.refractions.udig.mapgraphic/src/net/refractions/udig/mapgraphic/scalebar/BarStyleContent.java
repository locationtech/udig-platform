/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.scalebar;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicPlugin;
import net.refractions.udig.mapgraphic.scalebar.BarStyle.BarType;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * A bar style representing the type of scalebar to be drawn include the color
 * and number of divisions.
 * 
 * @author egouge
 * @since 1.1.0
 */
public class BarStyleContent extends StyleContent {
    /** extension id */
    public static final String ID = "net.refractions.udig.mapgraphic.style.bartype"; //$NON-NLS-1$
    
    private static final String BARSTYLE = "BAR_STYLE"; //$NON-NLS-1$
    private static final String NUM_INTERVAL = "NUM_INTERVAL"; //$NON-NLS-1$
    private static final String COLOR_R = "R"; //$NON-NLS-1$
    private static final String COLOR_G = "G"; //$NON-NLS-1$
    private static final String COLOR_B = "B"; //$NON-NLS-1$
    private static final String UNITS = "UNITS"; //$NON-NLS-1$
    
    public BarStyleContent( ) {
        super(ID);
    }
    
    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor )
            throws IOException {
        if( !resource.canResolve(MapGraphic.class))
            return null;

        if( resource.canResolve(BarStyle.class) ){
            // lets assume this is the best location for this resource
            BarStyle style = resource.resolve(BarStyle.class, monitor);
            if( style!=null ){
                return style;
            }
        }
        
        return new BarStyle();
    }

    
    @Override
    public Class< ? > getStyleClass() {
        return BarStyle.class;
    }

    @Override
    public Object load( IMemento memento ) {
        try {
            Integer R = memento.getInteger(COLOR_R);
            Integer G = memento.getInteger(COLOR_G);
            Integer B = memento.getInteger(COLOR_B);
            int r = R == null ? 0 : R;
            int g = G == null ? 0 : G;
            int b = B == null ? 0 : B;
            
            Color c = new Color(r,g,b);
            int numintervales = memento.getInteger(NUM_INTERVAL);
            String bartype = memento.getString(BARSTYLE);
            //int units = memento.getInteger(UNITS);
            UnitPolicy units = UnitPolicy.valueOf(memento.getString(UNITS));
            BarStyle bs = new BarStyle(BarType.valueOf(bartype), c, numintervales, units);
            return bs;
        } catch (Throwable e) {
            MapGraphicPlugin.log("Error decoding the stored bar style", e); //$NON-NLS-1$
        }
        return new BarStyle();
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public void save( IMemento memento, Object value ) {
        BarStyle style = (BarStyle)value;
        memento.putString(BARSTYLE, style.getType().toString());
        memento.putInteger(NUM_INTERVAL, style.getNumintervals());
        memento.putInteger(COLOR_R, style.getColor().getRed());
        memento.putInteger(COLOR_G, style.getColor().getGreen());
        memento.putInteger(COLOR_B, style.getColor().getBlue());
        memento.putString(UNITS, style.getUnits().name());
    }

}
