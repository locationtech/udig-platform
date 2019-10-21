/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.property.pages;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.internal.CRSPropertyPage;

/**
 * A PropertyPage for setting the CRS on a map.
 * @author Jesse
 * @since 1.1.0
 */
public class MapCRSPropertyPage extends CRSPropertyPage implements IWorkbenchPropertyPage {

    /**
     * @param strategy
     */
    public MapCRSPropertyPage() {
    }

    @Override
    public void setElement( IAdaptable element ) {
        Map map;
        if( element instanceof Map){
            map=(Map)element;
        }else{
            map=(Map) element.getAdapter(Map.class);
        }
        super.setStrategy(new MapStrategy(map) );
    }
    
}
