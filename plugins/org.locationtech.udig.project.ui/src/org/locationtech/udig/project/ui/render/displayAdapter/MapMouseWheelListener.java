/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.render.displayAdapter;

/**
 * An Listener interested int Mouse Wheel Events. Does not work with the ViewportPaneSWT class. API
 * use
 * 
 * @author jeichar
 */
public interface MapMouseWheelListener {
    /**
     * called when the mouse wheel has moved.
     * 
     * @param e The mouse wheel event.
     * @see MapMouseWheelEvent
     */
    public void mouseWheelMoved( MapMouseWheelEvent e );
}
