/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.project.ILayer;

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
            decoration.addOverlay(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.CHANGED_OVR));
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
