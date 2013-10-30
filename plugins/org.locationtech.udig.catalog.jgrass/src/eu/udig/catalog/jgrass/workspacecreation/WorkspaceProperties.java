/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.jgrass.workspacecreation;

import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class WorkspaceProperties {
    public String locationPath = null;
    public String basePath = null;
    public String locationName = null;
    public String mapsetName = null;
    public String importFilePath = null;
    public CoordinateReferenceSystem crs = null;
    public double north = 100;
    public double south = 0;
    public double east = 100;
    public double west = 0;
    public double xres = 100;
    public double yres = 100;
    public List<String> mapsets = new ArrayList<String>();
}
