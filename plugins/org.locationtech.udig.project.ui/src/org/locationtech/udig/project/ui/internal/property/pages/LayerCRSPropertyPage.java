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
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.CRSPropertyPage;
import org.locationtech.udig.project.ui.internal.Messages;

/**
 * Property page for setting the crs of a {@link Layer}
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LayerCRSPropertyPage extends CRSPropertyPage implements IWorkbenchPropertyPage {

    public LayerCRSPropertyPage() {
        setMessage(Messages.LayerCRSPropertyPage_warningMessage, WARNING);
    }

    @Override
    public void setElement(final IAdaptable element) {
        Layer layer;
        if (element instanceof Layer) {
            layer = (Layer) element;
        } else {
            layer = (Layer) element.getAdapter(Layer.class);
        }
        super.setStrategy(new LayerStrategy(layer));
    }

}
