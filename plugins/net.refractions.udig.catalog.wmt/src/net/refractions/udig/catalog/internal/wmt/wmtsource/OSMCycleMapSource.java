/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wmt.wmtsource;

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
