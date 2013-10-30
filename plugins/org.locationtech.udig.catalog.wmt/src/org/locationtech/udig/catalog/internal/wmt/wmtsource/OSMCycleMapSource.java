/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.wmtsource;

public class OSMCycleMapSource extends OSMSource {
    public static String NAME = "Cycle Map"; //$NON-NLS-1$
    
    protected OSMCycleMapSource() {
        setName(NAME); 
    }

    @Override
    public String getBaseUrl() {
        return "http://andy.sandbox.cloudmade.com/tiles/cycle/"; //$NON-NLS-1$
    }

}
