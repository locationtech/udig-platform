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

import net.refractions.udig.catalog.internal.wmt.WMTPlugin;

public class OSMCloudMadeSource extends OSMSource{
    public static final String NAME = "CloudMade"; //$NON-NLS-1$
    private static final int DEFAULT_STYLE = 1;
    private int style;
            
    @Override
    protected void init(String resourceId) throws Exception {
        int style;
        
        try{
            style = Integer.parseInt(resourceId);
        } catch(Exception exc) {
            WMTPlugin.log("[OSMCloudMadeSource.init] Couldn't get the style-id, taking the default-id:", exc); //$NON-NLS-1$
            
            style = DEFAULT_STYLE; // set default style
        }
        
        this.style = style;
        
        setName(NAME + style);
    }

    @Override
    public String getBaseUrl() {
        return "http://tile.cloudmade.com/c8d1aeca771d57d6a0584fea7ce386f4/" + style + "/256/";  //$NON-NLS-1$//$NON-NLS-2$
    }

}
