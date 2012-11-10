/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.grid;

import net.refractions.udig.mapgraphic.AbstractToggleMapGraphicAction;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Adds or removes legend layers to/from a map
 * @author Jesse
 * @since 1.1.0
 */
public class AddGridAction extends AbstractToggleMapGraphicAction implements IWorkbenchWindowActionDelegate {

    protected Class<GridMapGraphic> getMapGraphicClass() {
        return GridMapGraphic.class;
    }

    protected String getExtensionID() {
        return "grid"; //$NON-NLS-1$
    }
    
    public void init( IWorkbenchWindow window ) {
    }

}
