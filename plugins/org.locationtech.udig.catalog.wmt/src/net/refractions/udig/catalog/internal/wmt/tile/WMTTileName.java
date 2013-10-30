/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
