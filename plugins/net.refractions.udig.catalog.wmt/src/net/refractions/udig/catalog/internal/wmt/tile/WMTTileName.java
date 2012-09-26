/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt.tile;

import java.net.URL;

import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;

public abstract class WMTTileName {
    public static final String ID_DIVIDER = "_"; //$NON-NLS-1$
        
    private WMTTile.WMTZoomLevel zoomLevel;
    private int x;
    private int y;
    private WMTSource source;
    
    public WMTTileName(WMTTile.WMTZoomLevel zoomLevel, int x, int y, WMTSource source) {
        this.zoomLevel = zoomLevel;
        this.x = x;
        this.y = y;
        this.source = source;
    }
    
    public int getZoomLevel() {
        return zoomLevel.getZoomLevel();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public WMTSource getSource() {
        return source;
    }
    
    public String getId() {
        return source.getId() + ID_DIVIDER + 
                getZoomLevel() + ID_DIVIDER + 
                getX() + ID_DIVIDER + 
                getY();
    }
    
    public abstract URL getTileUrl();
    
    /**
     * Arithmetic implementation of modulo,
     * as the Java implementation of modulo can return negative values.
     * <pre>
     * arithmeticMod(-1, 8) = 7
     * </pre>
     *
     * @param a
     * @param b
     * @return the positive remainder
     */
    public static int arithmeticMod(int a, int b) {
        return (a >= 0) ? a % b : a % b + b;
    }       
}
