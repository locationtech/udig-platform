/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

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
