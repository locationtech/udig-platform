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

import org.locationtech.udig.catalog.internal.wmt.WMTPlugin;

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
