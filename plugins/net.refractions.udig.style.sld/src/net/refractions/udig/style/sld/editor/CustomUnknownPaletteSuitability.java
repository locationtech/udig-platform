/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.style.sld.editor;

import java.io.IOException;

import org.geotools.brewer.color.PaletteSuitability;

/**
 * A {@link PaletteSuitability} singleton that works always and has an unknown status.
 * 
 * <p>
 * This can be used to not block the rendering process for palettes that
 * have no suitability definition.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CustomUnknownPaletteSuitability extends PaletteSuitability {
    private int[] unknownSuitabilityValues = {QUALITY_UNKNOWN, QUALITY_UNKNOWN, QUALITY_UNKNOWN, QUALITY_UNKNOWN, QUALITY_UNKNOWN,
            QUALITY_UNKNOWN};
    
    private static CustomUnknownPaletteSuitability suitability = null;
    private CustomUnknownPaletteSuitability(){
    }

    public static CustomUnknownPaletteSuitability getInstance(){
        if (suitability==null) {
            suitability = new CustomUnknownPaletteSuitability();
        }
        return suitability;
    }
    
    @Override
    public int[] getSuitability( int numClasses ) {
        return unknownSuitabilityValues;
    }

    public int getSuitability( int numClasses, int viewerType ) {
        return QUALITY_UNKNOWN;
    };
    
    @Override
    public void setSuitability( int numClasses, String[] suitability ) throws IOException {
        // do nothing
    }
    
    @Override
    public int getMaxColors() {
        // this seems to be never called, let's check
        throw new RuntimeException();
    }
}
