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
