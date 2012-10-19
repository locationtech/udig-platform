/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.graticule;

import net.refractions.udig.mapgraphic.AbstractToggleMapGraphicAction;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Adds or removes {@link GraticuleGraphic}
 *  
 * @author kengu
 * 
 * @since 1.3.3
 */
public class AddGraticuleAction extends AbstractToggleMapGraphicAction implements IWorkbenchWindowActionDelegate {

    protected Class<GraticuleGraphic> getMapGraphicClass() {
        return GraticuleGraphic.class;
    }

    public void init( IWorkbenchWindow window ) { }

    protected String getExtensionID() {
        return "graticule"; //$NON-NLS-1$
    }
    
}
