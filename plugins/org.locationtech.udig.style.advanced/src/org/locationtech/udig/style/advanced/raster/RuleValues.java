/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.raster;

import java.awt.Color;

import org.eclipse.swt.graphics.RGB;

/**
 * Simple container for color rules.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RuleValues {
    public double fromValue;
    public double toValue;
    public Color fromColor;
    public Color toColor;

    public static RGB asRGB( Color awtColor ) {
        return new RGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }
}
