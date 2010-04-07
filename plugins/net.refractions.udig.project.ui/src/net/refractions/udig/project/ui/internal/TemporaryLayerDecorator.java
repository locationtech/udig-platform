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
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.catalog.ITransientResolve;
import net.refractions.udig.project.ILayer;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * Indicates that the layer is a temporary layer by putting a start on the icon.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TemporaryLayerDecorator implements ILightweightLabelDecorator {

    public void decorate( Object element, IDecoration decoration ) {
        ILayer layer = (ILayer) element;
        if( layer.hasResource(ITransientResolve.class) ){
            decoration.addOverlay(Images.getDescriptor(ImageConstants.CHANGED_OVR));
        }
    }

    public void addListener( ILabelProviderListener listener ) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty( Object element, String property ) {
        return false;
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

}
