/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.scalebar;

import net.refractions.udig.mapgraphic.AbstractToggleMapGraphicAction;
import net.refractions.udig.mapgraphic.MapGraphic;

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
