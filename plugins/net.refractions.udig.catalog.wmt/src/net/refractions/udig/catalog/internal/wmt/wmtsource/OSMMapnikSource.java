/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.internal.wmt.wmtsource;

public class OSMMapnikSource extends OSMSource {
    public static String NAME = "Mapnik"; //$NON-NLS-1$
    
    protected OSMMapnikSource() {
        setName(NAME); 
    }

    @Override
    public String getBaseUrl() {
        return "http://tile.openstreetmap.org/"; //$NON-NLS-1$
    }
    

}
