/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.core;

import org.locationtech.jts.geom.Coordinate;

public class JGTtmsProperties {

    /**
     * Possible schemas
     */
    public static enum TILESCHEMA {
        tms, google
    }

    public String HOST_NAME;
    public String PROTOCOL = "http"; //$NON-NLS-1$
    public int ZOOM_MIN = 0;
    public int ZOOM_MAX = 18;

    public String tilePart;
    public boolean isFile = true;
    public Coordinate centerPoint = null;
    public TILESCHEMA type = TILESCHEMA.google;

}
