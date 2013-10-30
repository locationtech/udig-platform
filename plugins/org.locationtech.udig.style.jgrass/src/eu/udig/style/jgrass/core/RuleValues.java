/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.style.jgrass.core;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;

public class RuleValues {
    public double fromValue;
    public double toValue;
    public Color fromColor;
    public Color toColor;

    public static org.eclipse.swt.graphics.Color asSWT( Color awtColor ) {
        org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(Display
                .getDefault(), awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
        return color;
    }
}