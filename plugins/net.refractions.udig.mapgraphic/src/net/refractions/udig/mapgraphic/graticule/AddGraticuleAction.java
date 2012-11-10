/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
public class AddGraticuleAction extends AbstractToggleMapGraphicAction implements
        IWorkbenchWindowActionDelegate {

    @Override
    protected Class<GraticuleGraphic> getMapGraphicClass() {
        return GraticuleGraphic.class;
    }

    @Override
    public void init(IWorkbenchWindow window) {
    }

    @Override
    protected String getExtensionID() {
        return "graticule"; //$NON-NLS-1$
    }

}
