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

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.CRSPropertyPage;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPropertyPage;

/**
 * Property page for setting the crs of a {@link Layer}
 * @author Jesse
 * @since 1.1.0
 */
public class LayerCRSPropertyPage extends CRSPropertyPage implements IWorkbenchPropertyPage {

    public LayerCRSPropertyPage() {
        setMessage("Only change if the current projection is wrong.  Changes only affect how the data is interpreted, they are not modified,", WARNING);
    }
    
    @Override
    public void setElement( IAdaptable element ) {
        Layer layer;
        if( element instanceof Layer){
            layer=(Layer)element;
        }else{
            layer=(Layer) element.getAdapter(Layer.class);
        }
        super.setStrategy(new LayerStrategy(layer) );
    }
    
}
