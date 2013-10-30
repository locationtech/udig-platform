/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scalebar;

import org.locationtech.udig.mapgraphic.AbstractToggleMapGraphicAction;
import org.locationtech.udig.mapgraphic.MapGraphic;

import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Toggles the scalebar on and off
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ScalebarAction extends AbstractToggleMapGraphicAction
        implements
            IWorkbenchWindowActionDelegate {

    @Override
    protected Class< ? extends MapGraphic> getMapGraphicClass() {
        return ScalebarMapGraphic.class;
    }

    @Override
    protected String getExtensionID() {
        return "scalebar"; //$NON-NLS-1$
    }

}
