/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.activeregion;

import java.awt.Color;

public class ActiveRegionStyle {
    public Color foregroundColor;
    public Color backgroundColor;
    public float bAlpha;
    public float fAlpha;
    public boolean doGrid = false;
    public float north;
    public float south;
    public float east;
    public float west;
    public int rows;
    public int cols;
    public String crsString;
    public String windPath;
}
