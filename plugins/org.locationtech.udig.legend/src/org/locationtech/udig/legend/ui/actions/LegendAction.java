/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.legend.ui.actions;

import org.locationtech.udig.legend.ui.LegendGraphic;
import org.locationtech.udig.mapgraphic.AbstractToggleMapGraphicAction;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Adds or removes legend layers to/from a map
 * @author Jesse
 * @since 1.1.0
 */
public class LegendAction extends AbstractToggleMapGraphicAction implements IWorkbenchWindowActionDelegate {

    protected Class<LegendGraphic> getMapGraphicClass() {
        return LegendGraphic.class;
    }

    protected String getExtensionID() {
        return "legend"; //$NON-NLS-1$
    }
    
    public void init( IWorkbenchWindow window ) {
    }

}
