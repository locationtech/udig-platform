/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools;

import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.COLS;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.EAST;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.NORTH;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.ROWS;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.SOUTH;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.WEST;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.XRES;
import static org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools.CoverageUtilities.YRES;

import java.util.HashMap;

/**
 * Map containing a region definition, having utility methods to get the values.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 0.7.2
 */
public class RegionMap extends HashMap<String, Double> {
    private static final long serialVersionUID = 1L;

    /**
     * Getter for the region cols.
     * 
     * @return the region cols or -1.
     */
    public int getCols() {
        Double cols = get(COLS);
        if (cols != null) {
            return cols.intValue();
        }
        return -1;
    }

    /**
     * Getter for the region rows.
     * 
     * @return the region rows or -1.
     */
    public int getRows() {
        Double rows = get(ROWS);
        if (rows != null) {
            return rows.intValue();
        }
        return -1;
    }

    /**
     * Getter for the region's north bound.
     * 
     * @return the region north bound or {@link JGTConstants#doubleNovalue}
     */
    public double getNorth() {
        Double n = get(NORTH);
        if (n != null) {
            return n;
        }
        return JGTConstants.doubleNovalue;
    }

    /**
     * Getter for the region's south bound.
     * 
     * @return the region south bound or {@link JGTConstants#doubleNovalue}
     */
    public double getSouth() {
        Double s = get(SOUTH);
        if (s != null) {
            return s;
        }
        return JGTConstants.doubleNovalue;
    }

    /**
     * Getter for the region's east bound.
     * 
     * @return the region east bound or {@link JGTConstants#doubleNovalue}
     */
    public double getEast() {
        Double e = get(EAST);
        if (e != null) {
            return e;
        }
        return JGTConstants.doubleNovalue;
    }

    /**
     * Getter for the region's west bound.
     * 
     * @return the region west bound or {@link JGTConstants#doubleNovalue}
     */
    public double getWest() {
        Double w = get(WEST);
        if (w != null) {
            return w;
        }
        return JGTConstants.doubleNovalue;
    }

    /**
     * Getter for the region's X resolution.
     * 
     * @return the region's X resolution or {@link JGTConstants#doubleNovalue}
     */
    public double getXres() {
        Double xres = get(XRES);
        if (xres != null) {
            return xres;
        }
        return JGTConstants.doubleNovalue;
    }

    /**
     * Getter for the region's Y resolution.
     * 
     * @return the region's Y resolution or {@link JGTConstants#doubleNovalue}
     */
    public double getYres() {
        Double yres = get(YRES);
        if (yres != null) {
            return yres;
        }
        return JGTConstants.doubleNovalue;
    }
}
