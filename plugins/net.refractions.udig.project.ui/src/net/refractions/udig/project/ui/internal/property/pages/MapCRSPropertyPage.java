/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui.internal.property.pages;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.internal.CRSPropertyPage;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPropertyPage;

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
